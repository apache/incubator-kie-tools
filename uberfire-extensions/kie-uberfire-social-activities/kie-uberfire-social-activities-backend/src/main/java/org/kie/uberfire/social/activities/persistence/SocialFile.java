package org.kie.uberfire.social.activities.persistence;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardOpenOption;

public class SocialFile {

    private final Path path;
    private final Gson gson;
    private final String JSON_SEPARATOR = "01";
    private byte[] JSON_SEPARATOR_BYTES;
    private final IOService ioService;
    private SeekableByteChannel reader;
    private long currentCursorReadPosition;
    private ByteBuffer byteBufferSize;

    public SocialFile( Path path,
                       IOService ioService,
                       Gson gson ) {
        try {
            JSON_SEPARATOR_BYTES = Hex.decodeHex( JSON_SEPARATOR.toCharArray() );
        } catch ( DecoderException e ) {
            throw new ErrorReadingFile();
        }
        byteBufferSize = ByteBuffer.allocate( "a".getBytes().length );
        this.path = path;
        this.gson = gson;
        this.currentCursorReadPosition = -1;
        this.ioService = ioService;
    }

    public void write( List<SocialActivitiesEvent> events ) throws IOException {
        SeekableByteChannel sbc = ioService.newByteChannel( path );
        for ( SocialActivitiesEvent event : events ) {
            String json = gson.toJson( event );
            writeJson( json, sbc );
            writeSeparator( sbc );
            writeJsonSize( sbc, json );
            writeSeparator( sbc );
        }
        sbc.close();
    }

    private void writeJsonSize( SeekableByteChannel sbc,
                                String json ) throws IOException {
        String jsonLenght = String.valueOf( json.getBytes().length );
        ByteBuffer bfSrc = ByteBuffer.wrap( jsonLenght.getBytes() );
        sbc.write( bfSrc );
    }

    private void writeSeparator( SeekableByteChannel sbc ) throws IOException {
        ByteBuffer bfSrc = ByteBuffer.wrap( JSON_SEPARATOR_BYTES );
        sbc.write( bfSrc );
    }

    private void writeJson( String json,
                            SeekableByteChannel sbc ) throws IOException {
        ByteBuffer bfSrc = ByteBuffer.wrap( json.getBytes() );
        sbc.write( bfSrc );
    }

    public void prepareForReading() throws IOException {
        reader = ioService.newByteChannel( path, StandardOpenOption.READ );
        currentCursorReadPosition = reader.size() - 1;
    }

    private void reverseSearchForSeparatorPosition() throws IOException {
        if ( reader.size() > 0 ) {
            reader.position( currentCursorReadPosition );
            reader.read( byteBufferSize );
            byteBufferSize.flip();
            String s = new String( byteBufferSize.array() );
            if ( !lookforSeparator( byteBufferSize.array() ) ) {
                currentCursorReadPosition--;
                reverseSearchForSeparatorPosition();
            }
        }
    }

    public List<SocialActivitiesEvent> readSocialEvents( Integer numberOfEvents ) {
        List<SocialActivitiesEvent> events = new ArrayList<SocialActivitiesEvent>();

        if ( ioService.exists( path ) ) {
            for ( int i = 0; i < numberOfEvents; i++ ) {
                try {
                    String json = readNextSocialEventJSON();
                    SocialActivitiesEvent event = gson.fromJson( json, SocialActivitiesEvent.class );
                    events.add( event );
                } catch ( Exception e ) {
                    //ignore json error, try read next
                }
            }
        }
        return events;
    }

    private String readNextSocialEventJSON() throws IOException {
        if ( startReading() ) {
            prepareForReading();
        }
        if ( reader.size() <= 0 ) {
            throw new EmptySocialFile();
        }

        reverseSearchForSeparatorPosition();

        StringBuilder numberOfBytesNextJSON = getNumberOfBytesOfJSON();

        if ( StringUtils.isNumeric( numberOfBytesNextJSON.toString() ) ) {
            return extractJSON( numberOfBytesNextJSON );
        } else {
            return readNextSocialEventJSON();
        }
    }

    private String extractJSON(
            StringBuilder numberOfBytesNextJSON ) throws IOException {

        Integer numberOfBytes = getNumberOfBytesToReadJSON( numberOfBytesNextJSON );
        putCursorInRightPosition( numberOfBytes );

        return readJSON( numberOfBytes );
    }

    private String readJSON( Integer numberOfBytes ) throws IOException {
        ByteBuffer jsonByteBuffer = ByteBuffer.allocate( numberOfBytes.intValue() );
        StringBuilder extractedJSON = new StringBuilder();
        while ( ( reader.read( jsonByteBuffer ) ) > 0 ) {
            extractedJSON.append( new String( jsonByteBuffer.array() ) );
        }
        return extractedJSON.toString();
    }

    private void putCursorInRightPosition( Integer numberOfBytes ) throws IOException {
        currentCursorReadPosition = currentCursorReadPosition - numberOfBytes.intValue();
        reader.position( currentCursorReadPosition );
    }

    private Integer getNumberOfBytesToReadJSON( StringBuilder numberOfBytesNextJSON ) {
        Integer numberOfBytes = new Integer( numberOfBytesNextJSON.toString() );
        return numberOfBytes;
    }

    private StringBuilder getNumberOfBytesOfJSON() throws IOException {
        currentCursorReadPosition = currentCursorReadPosition - 1;
        reader.position( currentCursorReadPosition );

        StringBuilder numberOfBytesNextJSON = new StringBuilder();
        if ( thereIsSomethingToRead( reader, byteBufferSize ) ) {
            byteBufferSize.flip();

            if ( !lookforSeparator( byteBufferSize.array() ) ) {
                String charRead = new String( byteBufferSize.array() );
                numberOfBytesNextJSON.append( charRead );
                currentCursorReadPosition = currentCursorReadPosition - 1;
                reader.position( currentCursorReadPosition );
                while ( thereIsSomethingToRead( reader, byteBufferSize ) ) {
                    byteBufferSize.flip();
                    charRead = new String( byteBufferSize.array() );
                    if ( lookforSeparator( byteBufferSize.array() ) ) {
                        break;
                    }
                    numberOfBytesNextJSON.append( charRead );
                    byteBufferSize.clear();
                    currentCursorReadPosition = currentCursorReadPosition - 1;
                    reader.position( currentCursorReadPosition );
                }
            }
        }
        numberOfBytesNextJSON = numberOfBytesNextJSON.reverse();
        return numberOfBytesNextJSON;
    }

    private boolean startReading() {
        return reader == null || currentCursorReadPosition <= 0;
    }

    private boolean thereIsSomethingToRead( SeekableByteChannel sbc,
                                            ByteBuffer bf ) throws IOException {
        int i;
        return ( i = sbc.read( bf ) ) > 0;
    }

    private boolean lookforSeparator( byte[] charRead ) {
        String hexString = Hex.encodeHexString( charRead );
        return hexString.equalsIgnoreCase( JSON_SEPARATOR );
    }

    private class EmptySocialFile extends RuntimeException {

    }

    private class ErrorReadingFile extends RuntimeException {

    }
}
