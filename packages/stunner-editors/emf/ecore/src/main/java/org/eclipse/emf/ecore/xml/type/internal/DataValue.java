/**
 * Copyright (c) 2003-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * ---------------------------------------------------------------------
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.eclipse.emf.ecore.xml.type.internal;


import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

/**
 * NOTE: this class is for internal use only.
 */
public final class DataValue
{

static class ValidationContext
{
  // Empty.
}

static class XSSimpleType
{
  // Empty.
}

/*
 * This class provides encode/decode for RFC 2045 Base64 as
 * defined by RFC 2045, N. Freed and N. Borenstein.
 * RFC 2045: Multipurpose Internet Mail Extensions (MIME)
 * Part One: Format of Internet Message Bodies. Reference
 * 1996 Available at: http://www.ietf.org/rfc/rfc2045.txt
 * This class is used by XML Schema binary format validation
 *
 * This implementation does not encode/decode streaming
 * data. You need the data that you will encode/decode
 * already on a byte array.
 *
 * @author Jeffrey Rodriguez
 * @author Sandy Gao
 */
public static final class  Base64 {

  static private final int  BASELENGTH         = 255;
  static private final int  LOOKUPLENGTH       = 64;
  static private final int  TWENTYFOURBITGROUP = 24;
  static private final int  EIGHTBIT           = 8;
  static private final int  SIXTEENBIT         = 16;
  static private final int  FOURBYTE           = 4;
  static private final int  SIGN               = -128;
  static private final char PAD                = '=';
  static private final boolean fDebug          = false;
  static final private byte [] base64Alphabet        = new byte[BASELENGTH];
  static final private char [] lookUpBase64Alphabet  = new char[LOOKUPLENGTH];

  static {

      for (int i = 0; i<BASELENGTH; i++) {
          base64Alphabet[i] = -1;
      }
      for (int i = 'Z'; i >= 'A'; i--) {
          base64Alphabet[i] = (byte) (i-'A');
      }
      for (int i = 'z'; i>= 'a'; i--) {
          base64Alphabet[i] = (byte) ( i-'a' + 26);
      }

      for (int i = '9'; i >= '0'; i--) {
          base64Alphabet[i] = (byte) (i-'0' + 52);
      }

      base64Alphabet['+']  = 62;
      base64Alphabet['/']  = 63;

      for (int i = 0; i<=25; i++)
          lookUpBase64Alphabet[i] = (char)('A'+i);

      for (int i = 26,  j = 0; i<=51; i++, j++)
          lookUpBase64Alphabet[i] = (char)('a'+ j);

      for (int i = 52,  j = 0; i<=61; i++, j++)
          lookUpBase64Alphabet[i] = (char)('0' + j);
      lookUpBase64Alphabet[62] = '+';
      lookUpBase64Alphabet[63] = '/';

  }

  protected static boolean isWhiteSpace(char octect) {
      return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
  }

  protected static boolean isPad(char octect) {
      return (octect == PAD);
  }

  protected static boolean isData(char octect) {
      return (base64Alphabet[octect] != -1);
  }

  protected static boolean isBase64(char octect) {
      return (isWhiteSpace(octect) || isPad(octect) || isData(octect));
  }

  /**
   * Encodes hex octects into Base64
   *
   * @param binaryData Array containing binaryData
   * @return Encoded Base64 array
   */
  public static String encode(byte[] binaryData) {

      // This implementation was changed to not introduce multi line content.
    
      if (binaryData == null)
          return null;

      int      lengthDataBits    = binaryData.length*EIGHTBIT;
      if (lengthDataBits == 0) {
          return "";
      }
      
      int      fewerThan24bits   = lengthDataBits%TWENTYFOURBITGROUP;
      int      numberTriplets    = lengthDataBits/TWENTYFOURBITGROUP;
      int      numberQuartet     = fewerThan24bits != 0 ? numberTriplets+1 : numberTriplets;
      char     encodedData[]     = null;

      encodedData = new char[numberQuartet*4];

      byte k=0, l=0, b1=0,b2=0,b3=0;

      int encodedIndex = 0;
      int dataIndex   = 0;
      if (fDebug) {
          System.out.println("number of triplets = " + numberTriplets );
      }

      for (int i=0; i<numberTriplets; i++) {
          b1 = binaryData[dataIndex++];
          b2 = binaryData[dataIndex++];
          b3 = binaryData[dataIndex++];

          if (fDebug) {
              System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
          }

          l  = (byte)(b2 & 0x0f);
          k  = (byte)(b1 & 0x03);

          byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);

          byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
          byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);

          if (fDebug) {
              System.out.println( "val2 = " + val2 );
              System.out.println( "k4   = " + (k<<4));
              System.out.println( "vak  = " + (val2 | (k<<4)));
          }

          encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
          encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
          encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
          encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];
      }

      // form integral number of 6-bit groups
      if (fewerThan24bits == EIGHTBIT) {
          b1 = binaryData[dataIndex];
          k = (byte) ( b1 &0x03 );
          if (fDebug) {
              System.out.println("b1=" + b1);
              System.out.println("b1<<2 = " + (b1>>2) );
          }
          byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
          encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
          encodedData[encodedIndex++] = lookUpBase64Alphabet[ k<<4 ];
          encodedData[encodedIndex++] = PAD;
          encodedData[encodedIndex++] = PAD;
      } else if (fewerThan24bits == SIXTEENBIT) {
          b1 = binaryData[dataIndex];
          b2 = binaryData[dataIndex +1 ];
          l = ( byte ) ( b2 &0x0f );
          k = ( byte ) ( b1 &0x03 );

          byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
          byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);

          encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
          encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
          encodedData[encodedIndex++] = lookUpBase64Alphabet[ l<<2 ];
          encodedData[encodedIndex++] = PAD;
      }

      //encodedData[encodedIndex] = 0xa;
      
      return new String(encodedData);
  }

  /**
   * Decodes Base64 data into octects
   *
   * @param encoded
   * @return Array containind decoded data.
   */
  public static byte[] decode(String encoded) {

      if (encoded == null)
          return null;

      char[] base64Data = encoded.toCharArray();
      // remove white spaces
      int len = removeWhiteSpace(base64Data);
      
      if (len%FOURBYTE != 0) {
          return null;//should be divisible by four
      }

      int      numberQuadruple    = (len/FOURBYTE );

      if (numberQuadruple == 0)
          return new byte[0];

      byte     decodedData[]      = null;
      byte     b1=0,b2=0,b3=0, b4=0;
      char     d1=0,d2=0,d3=0,d4=0;

      int i = 0;
      int encodedIndex = 0;
      int dataIndex    = 0;
      decodedData      = new byte[ (numberQuadruple)*3];

      for (; i<numberQuadruple-1; i++) {

          if (!isData( (d1 = base64Data[dataIndex++]) )||
              !isData( (d2 = base64Data[dataIndex++]) )||
              !isData( (d3 = base64Data[dataIndex++]) )||
              !isData( (d4 = base64Data[dataIndex++]) ))
              return null;//if found "no data" just return null

          b1 = base64Alphabet[d1];
          b2 = base64Alphabet[d2];
          b3 = base64Alphabet[d3];
          b4 = base64Alphabet[d4];

          decodedData[encodedIndex++] = (byte)(  b1 <<2 | b2>>4 ) ;
          decodedData[encodedIndex++] = (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) );
          decodedData[encodedIndex++] = (byte)( b3<<6 | b4 );
      }

      if (!isData( (d1 = base64Data[dataIndex++]) ) ||
          !isData( (d2 = base64Data[dataIndex++]) )) {
          return null;//if found "no data" just return null
      }

      b1 = base64Alphabet[d1];
      b2 = base64Alphabet[d2];

      d3 = base64Data[dataIndex++];
      d4 = base64Data[dataIndex++];
      if (!isData( (d3 ) ) ||
          !isData( (d4 ) )) {//Check if they are PAD characters
          if (isPad( d3 ) && isPad( d4)) {               //Two PAD e.g. 3c[Pad][Pad]
              if ((b2 & 0xf) != 0)//last 4 bits should be zero
                  return null;
              byte[] tmp = new byte[ i*3 + 1 ];
              System.arraycopy( decodedData, 0, tmp, 0, i*3 );
              tmp[encodedIndex]   = (byte)(  b1 <<2 | b2>>4 ) ;
              return tmp;
          } else if (!isPad( d3) && isPad(d4)) {               //One PAD  e.g. 3cQ[Pad]
              b3 = base64Alphabet[ d3 ];
              if ((b3 & 0x3 ) != 0)//last 2 bits should be zero
                  return null;
              byte[] tmp = new byte[ i*3 + 2 ];
              System.arraycopy( decodedData, 0, tmp, 0, i*3 );
              tmp[encodedIndex++] = (byte)(  b1 <<2 | b2>>4 );
              tmp[encodedIndex]   = (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) );
              return tmp;
          } else {
              return null;//an error  like "3c[Pad]r", "3cdX", "3cXd", "3cXX" where X is non data
          }
      } else { //No PAD e.g 3cQl
          b3 = base64Alphabet[ d3 ];
          b4 = base64Alphabet[ d4 ];
          decodedData[encodedIndex++] = (byte)(  b1 <<2 | b2>>4 ) ;
          decodedData[encodedIndex++] = (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) );
          decodedData[encodedIndex++] = (byte)( b3<<6 | b4 );

      }

      return decodedData;
  }

  /**
   * remove WhiteSpace from MIME containing encoded Base64 data.
   * 
   * @param data  the byte array of base64 data (with WS)
   * @return      the new length
   */
  protected static int removeWhiteSpace(char[] data) {
      if (data == null)
          return 0;

      // count characters that's not whitespace
      int newSize = 0;
      int len = data.length;
      for (int i = 0; i < len; i++) {
          if (!isWhiteSpace(data[i]))
              data[newSize++] = data[i];
      }
      return newSize;
  }
}


/*
 * format validation
 *
 * This class encodes/decodes hexadecimal data
 * @author Jeffrey Rodriguez
 */
public static final class  HexBin {
  static private final int  BASELENGTH   = 255;
  static private final int  LOOKUPLENGTH = 16;
  static final private byte [] hexNumberTable    = new byte[BASELENGTH];
  static final private char [] lookUpHexAlphabet = new char[LOOKUPLENGTH];


  static {
      for (int i = 0; i<BASELENGTH; i++ ) {
          hexNumberTable[i] = -1;
      }
      for ( int i = '9'; i >= '0'; i--) {
          hexNumberTable[i] = (byte) (i-'0');
      }
      for ( int i = 'F'; i>= 'A'; i--) {
          hexNumberTable[i] = (byte) ( i-'A' + 10 );
      }
      for ( int i = 'f'; i>= 'a'; i--) {
         hexNumberTable[i] = (byte) ( i-'a' + 10 );
      }

      for(int i = 0; i<10; i++ )
          lookUpHexAlphabet[i] = (char)('0'+i);
      for(int i = 10; i<=15; i++ )
          lookUpHexAlphabet[i] = (char)('A'+i -10);
  }

  /**
   * Encode a byte array to hex string
   *
   * @param binaryData  array of byte to encode
   * @return return     encoded string
   */
  static public String encode(byte[] binaryData) {
      if (binaryData == null)
          return null;
      int lengthData   = binaryData.length;
      int lengthEncode = lengthData * 2;
      char[] encodedData = new char[lengthEncode];
      int temp;
      for (int i = 0; i < lengthData; i++) {
          temp = binaryData[i];
          if (temp < 0)
              temp += 256;
          encodedData[i*2] = lookUpHexAlphabet[temp >> 4];
          encodedData[i*2+1] = lookUpHexAlphabet[temp & 0xf];
      }
      return new String(encodedData);
  }

  /**
   * Decode hex string to a byte array
   *
   * @param encoded  encoded string
   * @return return     array of byte to encode
   */
  static public byte[] decode(String encoded) {
      if (encoded == null)
          return null;
      int lengthData = encoded.length();
      if (lengthData % 2 != 0)
          return null;

      char[] binaryData = encoded.toCharArray();
      int lengthDecode = lengthData / 2;
      byte[] decodedData = new byte[lengthDecode];
      byte temp1, temp2;
      for( int i = 0; i<lengthDecode; i++ ){
          temp1 = hexNumberTable[binaryData[i*2]];
          if (temp1 == -1)
              return null;
          temp2 = hexNumberTable[binaryData[i*2+1]];
          if (temp2 == -1)
              return null;
          decodedData[i] = (byte)((temp1 << 4) | temp2);
      }
      return decodedData;
  }
}

/*
 * EncodingMap is a convenience class which handles conversions between 
 * IANA encoding names and Java encoding names, and vice versa. The
 * encoding names used in XML instance documents <strong>must</strong>
 * be the IANA encoding names specified or one of the aliases for those names
 * which IANA defines.
 * <p>
 * <TABLE BORDER="0" WIDTH="100%">
 *  <TR>
 *      <TD WIDTH="33%">
 *          <P ALIGN="CENTER"><B>Common Name</B>
 *      </TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER"><B>Use this name in XML files</B>
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER"><B>Name Type</B>
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER"><B>Xerces converts to this Java Encoder Name</B>
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">8 bit Unicode</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">UTF-8
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">UTF8
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 1</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-1
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-1
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 2</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-2
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-2
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 3</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-3
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-3
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 4</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-4
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-4
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin Cyrillic</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-5
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-5
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin Arabic</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-6
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-6
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin Greek</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-7
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-7
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin Hebrew</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-8
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-8
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">ISO Latin 5</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ISO-8859-9
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">ISO-8859-9
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: US</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-us
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp037
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Canada</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-ca
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp037
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Netherlands</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-nl
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp037
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Denmark</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-dk
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp277
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Norway</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-no
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp277
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Finland</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-fi
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp278
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Sweden</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-se
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp278
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Italy</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-it
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp280
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Spain, Latin America</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-es
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp284
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Great Britain</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-gb
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp285
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: France</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-fr
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp297
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Arabic</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-ar1
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp420
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Hebrew</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-he
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp424
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Switzerland</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-ch
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp500
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Roece</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-roece
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp870
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Yugoslavia</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-yu
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp870
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Iceland</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-is
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp871
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">EBCDIC: Urdu</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">ebcdic-cp-ar2
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">IANA
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">cp918
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Chinese for PRC, mixed 1/2 byte</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">gb2312
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">GB2312
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Extended Unix Code, packed for Japanese</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">euc-jp
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">eucjis
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Japanese: iso-2022-jp</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">iso-2020-jp
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">JIS
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Japanese: Shift JIS</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">Shift_JIS
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">SJIS
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Chinese: Big5</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">Big5
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">Big5
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Extended Unix Code, packed for Korean</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">euc-kr
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">iso2022kr
 *      </TD>
 *  </TR>
 *  <TR>
 *      <TD WIDTH="33%">Cyrillic</TD>
 *      <TD WIDTH="15%">
 *          <P ALIGN="CENTER">koi8-r
 *      </TD>
 *      <TD WIDTH="12%">
 *          <P ALIGN="CENTER">MIME
 *      </TD>
 *      <TD WIDTH="31%">
 *          <P ALIGN="CENTER">koi8-r
 *      </TD>
 *  </TR>
 * </TABLE>
 * 
 * @author TAMURA Kent, IBM
 * @author Andy Clark, IBM
 */
public static class EncodingMap {

    //
    // Data
    //

    /** fIANA2JavaMap */
    protected final static HashMap<String, String> fIANA2JavaMap = new HashMap<String, String>();

    /** fJava2IANAMap */
    protected final static HashMap<String, String> fJava2IANAMap = new HashMap<String, String>();

    //
    // Static initialization
    //

    static {

        // add IANA to Java encoding mappings.
        fIANA2JavaMap.put("BIG5",            "Big5");
        fIANA2JavaMap.put("CSBIG5",            "Big5");
        fIANA2JavaMap.put("CP037",    "CP037");
        fIANA2JavaMap.put("IBM037",    "CP037");
        fIANA2JavaMap.put("CSIBM037",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-US",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-CA",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-NL",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-WT",    "CP037");
        fIANA2JavaMap.put("IBM273",    "CP273");
        fIANA2JavaMap.put("CP273",    "CP273");
        fIANA2JavaMap.put("CSIBM273",    "CP273");
        fIANA2JavaMap.put("IBM277",    "CP277");
        fIANA2JavaMap.put("CP277",    "CP277");
        fIANA2JavaMap.put("CSIBM277",    "CP277");
        fIANA2JavaMap.put("EBCDIC-CP-DK",    "CP277");
        fIANA2JavaMap.put("EBCDIC-CP-NO",    "CP277");
        fIANA2JavaMap.put("IBM278",    "CP278");
        fIANA2JavaMap.put("CP278",    "CP278");
        fIANA2JavaMap.put("CSIBM278",    "CP278");
        fIANA2JavaMap.put("EBCDIC-CP-FI",    "CP278");
        fIANA2JavaMap.put("EBCDIC-CP-SE",    "CP278");
        fIANA2JavaMap.put("IBM280",    "CP280");
        fIANA2JavaMap.put("CP280",    "CP280");
        fIANA2JavaMap.put("CSIBM280",    "CP280");
        fIANA2JavaMap.put("EBCDIC-CP-IT",    "CP280");
        fIANA2JavaMap.put("IBM284",    "CP284");
        fIANA2JavaMap.put("CP284",    "CP284");
        fIANA2JavaMap.put("CSIBM284",    "CP284");
        fIANA2JavaMap.put("EBCDIC-CP-ES",    "CP284");
        fIANA2JavaMap.put("EBCDIC-CP-GB",    "CP285");
        fIANA2JavaMap.put("IBM285",    "CP285");
        fIANA2JavaMap.put("CP285",    "CP285");
        fIANA2JavaMap.put("CSIBM285",    "CP285");
        fIANA2JavaMap.put("EBCDIC-JP-KANA",    "CP290");
        fIANA2JavaMap.put("IBM290",    "CP290");
        fIANA2JavaMap.put("CP290",    "CP290");
        fIANA2JavaMap.put("CSIBM290",    "CP290");
        fIANA2JavaMap.put("EBCDIC-CP-FR",    "CP297");
        fIANA2JavaMap.put("IBM297",    "CP297");
        fIANA2JavaMap.put("CP297",    "CP297");
        fIANA2JavaMap.put("CSIBM297",    "CP297");
        fIANA2JavaMap.put("EBCDIC-CP-AR1",   "CP420");
        fIANA2JavaMap.put("IBM420",    "CP420");
        fIANA2JavaMap.put("CP420",    "CP420");
        fIANA2JavaMap.put("CSIBM420",    "CP420");
        fIANA2JavaMap.put("EBCDIC-CP-HE",    "CP424");
        fIANA2JavaMap.put("IBM424",    "CP424");
        fIANA2JavaMap.put("CP424",    "CP424");
        fIANA2JavaMap.put("CSIBM424",    "CP424");
        fIANA2JavaMap.put("IBM437",    "CP437");
        fIANA2JavaMap.put("437",    "CP437");
        fIANA2JavaMap.put("CP437",    "CP437");
        fIANA2JavaMap.put("CSPC8CODEPAGE437",    "CP437");
        fIANA2JavaMap.put("EBCDIC-CP-CH",    "CP500");
        fIANA2JavaMap.put("IBM500",    "CP500");
        fIANA2JavaMap.put("CP500",    "CP500");
        fIANA2JavaMap.put("CSIBM500",    "CP500");
        fIANA2JavaMap.put("EBCDIC-CP-CH",    "CP500");
        fIANA2JavaMap.put("EBCDIC-CP-BE",    "CP500"); 
        fIANA2JavaMap.put("IBM775",    "CP775");
        fIANA2JavaMap.put("CP775",    "CP775");
        fIANA2JavaMap.put("CSPC775BALTIC",    "CP775");
        fIANA2JavaMap.put("IBM850",    "CP850");
        fIANA2JavaMap.put("850",    "CP850");
        fIANA2JavaMap.put("CP850",    "CP850");
        fIANA2JavaMap.put("CSPC850MULTILINGUAL",    "CP850");
        fIANA2JavaMap.put("IBM852",    "CP852");
        fIANA2JavaMap.put("852",    "CP852");
        fIANA2JavaMap.put("CP852",    "CP852");
        fIANA2JavaMap.put("CSPCP852",    "CP852");
        fIANA2JavaMap.put("IBM855",    "CP855");
        fIANA2JavaMap.put("855",    "CP855");
        fIANA2JavaMap.put("CP855",    "CP855");
        fIANA2JavaMap.put("CSIBM855",    "CP855");
        fIANA2JavaMap.put("IBM857",    "CP857");
        fIANA2JavaMap.put("857",    "CP857");
        fIANA2JavaMap.put("CP857",    "CP857");
        fIANA2JavaMap.put("CSIBM857",    "CP857");
        fIANA2JavaMap.put("IBM00858",    "CP858");
        fIANA2JavaMap.put("CP00858",    "CP858");
        fIANA2JavaMap.put("CCSID00858",    "CP858");
        fIANA2JavaMap.put("IBM860",    "CP860");
        fIANA2JavaMap.put("860",    "CP860");
        fIANA2JavaMap.put("CP860",    "CP860");
        fIANA2JavaMap.put("CSIBM860",    "CP860");
        fIANA2JavaMap.put("IBM861",    "CP861");
        fIANA2JavaMap.put("861",    "CP861");
        fIANA2JavaMap.put("CP861",    "CP861");
        fIANA2JavaMap.put("CP-IS",    "CP861");
        fIANA2JavaMap.put("CSIBM861",    "CP861");
        fIANA2JavaMap.put("IBM862",    "CP862");
        fIANA2JavaMap.put("862",    "CP862");
        fIANA2JavaMap.put("CP862",    "CP862");
        fIANA2JavaMap.put("CSPC862LATINHEBREW",    "CP862");
        fIANA2JavaMap.put("IBM863",    "CP863");
        fIANA2JavaMap.put("863",    "CP863");
        fIANA2JavaMap.put("CP863",    "CP863");
        fIANA2JavaMap.put("CSIBM863",    "CP863");
        fIANA2JavaMap.put("IBM864",    "CP864");
        fIANA2JavaMap.put("CP864",    "CP864");
        fIANA2JavaMap.put("CSIBM864",    "CP864");
        fIANA2JavaMap.put("IBM865",    "CP865");
        fIANA2JavaMap.put("865",    "CP865");
        fIANA2JavaMap.put("CP865",    "CP865");
        fIANA2JavaMap.put("CSIBM865",    "CP865");
        fIANA2JavaMap.put("IBM866",    "CP866");
        fIANA2JavaMap.put("866",    "CP866");
        fIANA2JavaMap.put("CP866",    "CP866");
        fIANA2JavaMap.put("CSIBM866",    "CP866");
        fIANA2JavaMap.put("IBM868",    "CP868");
        fIANA2JavaMap.put("CP868",    "CP868");
        fIANA2JavaMap.put("CSIBM868",    "CP868");
        fIANA2JavaMap.put("CP-AR",        "CP868");
        fIANA2JavaMap.put("IBM869",    "CP869");
        fIANA2JavaMap.put("CP869",    "CP869");
        fIANA2JavaMap.put("CSIBM869",    "CP869");
        fIANA2JavaMap.put("CP-GR",        "CP869");
        fIANA2JavaMap.put("IBM870",    "CP870");
        fIANA2JavaMap.put("CP870",    "CP870");
        fIANA2JavaMap.put("CSIBM870",    "CP870");
        fIANA2JavaMap.put("EBCDIC-CP-ROECE", "CP870");
        fIANA2JavaMap.put("EBCDIC-CP-YU",    "CP870");
        fIANA2JavaMap.put("IBM871",    "CP871");
        fIANA2JavaMap.put("CP871",    "CP871");
        fIANA2JavaMap.put("CSIBM871",    "CP871");
        fIANA2JavaMap.put("EBCDIC-CP-IS",    "CP871");
        fIANA2JavaMap.put("IBM918",    "CP918");
        fIANA2JavaMap.put("CP918",    "CP918");
        fIANA2JavaMap.put("CSIBM918",    "CP918");
        fIANA2JavaMap.put("EBCDIC-CP-AR2",   "CP918");
        fIANA2JavaMap.put("IBM00924",    "CP924");
        fIANA2JavaMap.put("CP00924",    "CP924");
        fIANA2JavaMap.put("CCSID00924",    "CP924");
        // is this an error???
        fIANA2JavaMap.put("EBCDIC-LATIN9--EURO",    "CP924");
        fIANA2JavaMap.put("IBM1026",    "CP1026");
        fIANA2JavaMap.put("CP1026",    "CP1026");
        fIANA2JavaMap.put("CSIBM1026",    "CP1026");
        fIANA2JavaMap.put("IBM01140",    "Cp1140");
        fIANA2JavaMap.put("CP01140",    "Cp1140");
        fIANA2JavaMap.put("CCSID01140",    "Cp1140");
        fIANA2JavaMap.put("IBM01141",    "Cp1141");
        fIANA2JavaMap.put("CP01141",    "Cp1141");
        fIANA2JavaMap.put("CCSID01141",    "Cp1141");
        fIANA2JavaMap.put("IBM01142",    "Cp1142");
        fIANA2JavaMap.put("CP01142",    "Cp1142");
        fIANA2JavaMap.put("CCSID01142",    "Cp1142");
        fIANA2JavaMap.put("IBM01143",    "Cp1143");
        fIANA2JavaMap.put("CP01143",    "Cp1143");
        fIANA2JavaMap.put("CCSID01143",    "Cp1143");
        fIANA2JavaMap.put("IBM01144",    "Cp1144");
        fIANA2JavaMap.put("CP01144",    "Cp1144");
        fIANA2JavaMap.put("CCSID01144",    "Cp1144");
        fIANA2JavaMap.put("IBM01145",    "Cp1145");
        fIANA2JavaMap.put("CP01145",    "Cp1145");
        fIANA2JavaMap.put("CCSID01145",    "Cp1145");
        fIANA2JavaMap.put("IBM01146",    "Cp1146");
        fIANA2JavaMap.put("CP01146",    "Cp1146");
        fIANA2JavaMap.put("CCSID01146",    "Cp1146");
        fIANA2JavaMap.put("IBM01147",    "Cp1147");
        fIANA2JavaMap.put("CP01147",    "Cp1147");
        fIANA2JavaMap.put("CCSID01147",    "Cp1147");
        fIANA2JavaMap.put("IBM01148",    "Cp1148");
        fIANA2JavaMap.put("CP01148",    "Cp1148");
        fIANA2JavaMap.put("CCSID01148",    "Cp1148");
        fIANA2JavaMap.put("IBM01149",    "Cp1149");
        fIANA2JavaMap.put("CP01149",    "Cp1149");
        fIANA2JavaMap.put("CCSID01149",    "Cp1149");
        fIANA2JavaMap.put("EUC-JP",          "EUCJIS");
        fIANA2JavaMap.put("CSEUCPKDFMTJAPANESE",          "EUCJIS");
        fIANA2JavaMap.put("EXTENDED_UNIX_CODE_PACKED_FORMAT_FOR_JAPANESE",          "EUCJIS");
        fIANA2JavaMap.put("EUC-KR",          "KSC5601");
        fIANA2JavaMap.put("GB2312",          "GB2312");
        fIANA2JavaMap.put("CSGB2312",          "GB2312");
        fIANA2JavaMap.put("ISO-2022-JP",     "JIS");
        fIANA2JavaMap.put("CSISO2022JP",     "JIS");
        fIANA2JavaMap.put("ISO-2022-KR",     "ISO2022KR");
        fIANA2JavaMap.put("CSISO2022KR",     "ISO2022KR");
        fIANA2JavaMap.put("ISO-2022-CN",     "ISO2022CN");

        fIANA2JavaMap.put("X0201",  "JIS0201");
        fIANA2JavaMap.put("CSISO13JISC6220JP", "JIS0201");
        fIANA2JavaMap.put("X0208",  "JIS0208");
        fIANA2JavaMap.put("ISO-IR-87",  "JIS0208");
        fIANA2JavaMap.put("X0208dbiJIS_X0208-1983",  "JIS0208");
        fIANA2JavaMap.put("CSISO87JISX0208",  "JIS0208");
        fIANA2JavaMap.put("X0212",  "JIS0212");
        fIANA2JavaMap.put("ISO-IR-159",  "JIS0212");
        fIANA2JavaMap.put("CSISO159JISX02121990",  "JIS0212");
        fIANA2JavaMap.put("GB18030",       "GB18030");
        fIANA2JavaMap.put("SHIFT_JIS",       "SJIS");
        fIANA2JavaMap.put("CSSHIFTJIS",       "SJIS");
        fIANA2JavaMap.put("MS_KANJI",       "SJIS");
        fIANA2JavaMap.put("WINDOWS-31J",       "MS932");
        fIANA2JavaMap.put("CSWINDOWS31J",       "MS932");

	    // Add support for Cp1252 and its friends
        fIANA2JavaMap.put("WINDOWS-1250",   "Cp1250");
        fIANA2JavaMap.put("WINDOWS-1251",   "Cp1251");
        fIANA2JavaMap.put("WINDOWS-1252",   "Cp1252");
        fIANA2JavaMap.put("WINDOWS-1253",   "Cp1253");
        fIANA2JavaMap.put("WINDOWS-1254",   "Cp1254");
        fIANA2JavaMap.put("WINDOWS-1255",   "Cp1255");
        fIANA2JavaMap.put("WINDOWS-1256",   "Cp1256");
        fIANA2JavaMap.put("WINDOWS-1257",   "Cp1257");
        fIANA2JavaMap.put("WINDOWS-1258",   "Cp1258");
        fIANA2JavaMap.put("TIS-620",   "TIS620");

        fIANA2JavaMap.put("ISO-8859-1",      "ISO8859_1"); 
        fIANA2JavaMap.put("ISO-IR-100",      "ISO8859_1");
        fIANA2JavaMap.put("ISO_8859-1",      "ISO8859_1");
        fIANA2JavaMap.put("LATIN1",      "ISO8859_1");
        fIANA2JavaMap.put("CSISOLATIN1",      "ISO8859_1");
        fIANA2JavaMap.put("L1",      "ISO8859_1");
        fIANA2JavaMap.put("IBM819",      "ISO8859_1");
        fIANA2JavaMap.put("CP819",      "ISO8859_1");

        fIANA2JavaMap.put("ISO-8859-2",      "ISO8859_2"); 
        fIANA2JavaMap.put("ISO-IR-101",      "ISO8859_2");
        fIANA2JavaMap.put("ISO_8859-2",      "ISO8859_2");
        fIANA2JavaMap.put("LATIN2",      "ISO8859_2");
        fIANA2JavaMap.put("CSISOLATIN2",      "ISO8859_2");
        fIANA2JavaMap.put("L2",      "ISO8859_2");

        fIANA2JavaMap.put("ISO-8859-3",      "ISO8859_3"); 
        fIANA2JavaMap.put("ISO-IR-109",      "ISO8859_3");
        fIANA2JavaMap.put("ISO_8859-3",      "ISO8859_3");
        fIANA2JavaMap.put("LATIN3",      "ISO8859_3");
        fIANA2JavaMap.put("CSISOLATIN3",      "ISO8859_3");
        fIANA2JavaMap.put("L3",      "ISO8859_3");

        fIANA2JavaMap.put("ISO-8859-4",      "ISO8859_4"); 
        fIANA2JavaMap.put("ISO-IR-110",      "ISO8859_4");
        fIANA2JavaMap.put("ISO_8859-4",      "ISO8859_4");
        fIANA2JavaMap.put("LATIN4",      "ISO8859_4");
        fIANA2JavaMap.put("CSISOLATIN4",      "ISO8859_4");
        fIANA2JavaMap.put("L4",      "ISO8859_4");

        fIANA2JavaMap.put("ISO-8859-5",      "ISO8859_5"); 
        fIANA2JavaMap.put("ISO-IR-144",      "ISO8859_5");
        fIANA2JavaMap.put("ISO_8859-5",      "ISO8859_5");
        fIANA2JavaMap.put("CYRILLIC",      "ISO8859_5");
        fIANA2JavaMap.put("CSISOLATINCYRILLIC",      "ISO8859_5");

        fIANA2JavaMap.put("ISO-8859-6",      "ISO8859_6"); 
        fIANA2JavaMap.put("ISO-IR-127",      "ISO8859_6");
        fIANA2JavaMap.put("ISO_8859-6",      "ISO8859_6");
        fIANA2JavaMap.put("ECMA-114",      "ISO8859_6");
        fIANA2JavaMap.put("ASMO-708",      "ISO8859_6");
        fIANA2JavaMap.put("ARABIC",      "ISO8859_6");
        fIANA2JavaMap.put("CSISOLATINARABIC",      "ISO8859_6");

        fIANA2JavaMap.put("ISO-8859-7",      "ISO8859_7"); 
        fIANA2JavaMap.put("ISO-IR-126",      "ISO8859_7");
        fIANA2JavaMap.put("ISO_8859-7",      "ISO8859_7");
        fIANA2JavaMap.put("ELOT_928",      "ISO8859_7");
        fIANA2JavaMap.put("ECMA-118",      "ISO8859_7");
        fIANA2JavaMap.put("GREEK",      "ISO8859_7");
        fIANA2JavaMap.put("CSISOLATINGREEK",      "ISO8859_7");
        fIANA2JavaMap.put("GREEK8",      "ISO8859_7");

        fIANA2JavaMap.put("ISO-8859-8",      "ISO8859_8"); 
        fIANA2JavaMap.put("ISO-8859-8-I",      "ISO8859_8"); // added since this encoding only differs w.r.t. presentation 
        fIANA2JavaMap.put("ISO-IR-138",      "ISO8859_8");
        fIANA2JavaMap.put("ISO_8859-8",      "ISO8859_8");
        fIANA2JavaMap.put("HEBREW",      "ISO8859_8");
        fIANA2JavaMap.put("CSISOLATINHEBREW",      "ISO8859_8");

        fIANA2JavaMap.put("ISO-8859-9",      "ISO8859_9"); 
        fIANA2JavaMap.put("ISO-IR-148",      "ISO8859_9");
        fIANA2JavaMap.put("ISO_8859-9",      "ISO8859_9");
        fIANA2JavaMap.put("LATIN5",      "ISO8859_9");
        fIANA2JavaMap.put("CSISOLATIN5",      "ISO8859_9");
        fIANA2JavaMap.put("L5",      "ISO8859_9");

        fIANA2JavaMap.put("KOI8-R",          "KOI8_R");
        fIANA2JavaMap.put("CSKOI8R",          "KOI8_R");
        fIANA2JavaMap.put("US-ASCII",        "ASCII"); 
        fIANA2JavaMap.put("ISO-IR-6",        "ASCII");
        fIANA2JavaMap.put("ANSI_X3.4-1986",        "ASCII");
        fIANA2JavaMap.put("ISO_646.IRV:1991",        "ASCII");
        fIANA2JavaMap.put("ASCII",        "ASCII");
        fIANA2JavaMap.put("CSASCII",        "ASCII");
        fIANA2JavaMap.put("ISO646-US",        "ASCII");
        fIANA2JavaMap.put("US",        "ASCII");
        fIANA2JavaMap.put("IBM367",        "ASCII");
        fIANA2JavaMap.put("CP367",        "ASCII");
        fIANA2JavaMap.put("UTF-8",           "UTF8");
        fIANA2JavaMap.put("UTF-16",           "Unicode");
        fIANA2JavaMap.put("UTF-16BE",           "UnicodeBig");
        fIANA2JavaMap.put("UTF-16LE",           "UnicodeLittle");

        // support for 1047, as proposed to be added to the 
        // IANA registry in 
        // http://lists.w3.org/Archives/Public/ietf-charset/2002JulSep/0049.html
        fIANA2JavaMap.put("IBM-1047",    "Cp1047");
        fIANA2JavaMap.put("IBM1047",    "Cp1047");
        fIANA2JavaMap.put("CP1047",    "Cp1047");

        // Adding new aliases as proposed in
        // http://lists.w3.org/Archives/Public/ietf-charset/2002JulSep/0058.html
        fIANA2JavaMap.put("IBM-37",    "CP037");
        fIANA2JavaMap.put("IBM-273",    "CP273");
        fIANA2JavaMap.put("IBM-277",    "CP277");
        fIANA2JavaMap.put("IBM-278",    "CP278");
        fIANA2JavaMap.put("IBM-280",    "CP280");
        fIANA2JavaMap.put("IBM-284",    "CP284");
        fIANA2JavaMap.put("IBM-285",    "CP285");
        fIANA2JavaMap.put("IBM-290",    "CP290");
        fIANA2JavaMap.put("IBM-297",    "CP297");
        fIANA2JavaMap.put("IBM-420",    "CP420");
        fIANA2JavaMap.put("IBM-424",    "CP424");
        fIANA2JavaMap.put("IBM-437",    "CP437");
        fIANA2JavaMap.put("IBM-500",    "CP500");
        fIANA2JavaMap.put("IBM-775",    "CP775");
        fIANA2JavaMap.put("IBM-850",    "CP850");
        fIANA2JavaMap.put("IBM-852",    "CP852");
        fIANA2JavaMap.put("IBM-855",    "CP855");
        fIANA2JavaMap.put("IBM-857",    "CP857");
        fIANA2JavaMap.put("IBM-858",    "CP858");
        fIANA2JavaMap.put("IBM-860",    "CP860");
        fIANA2JavaMap.put("IBM-861",    "CP861");
        fIANA2JavaMap.put("IBM-862",    "CP862");
        fIANA2JavaMap.put("IBM-863",    "CP863");
        fIANA2JavaMap.put("IBM-864",    "CP864");
        fIANA2JavaMap.put("IBM-865",    "CP865");
        fIANA2JavaMap.put("IBM-866",    "CP866");
        fIANA2JavaMap.put("IBM-868",    "CP868");
        fIANA2JavaMap.put("IBM-869",    "CP869");
        fIANA2JavaMap.put("IBM-870",    "CP870");
        fIANA2JavaMap.put("IBM-871",    "CP871");
        fIANA2JavaMap.put("IBM-918",    "CP918");
        fIANA2JavaMap.put("IBM-924",    "CP924");
        fIANA2JavaMap.put("IBM-1026",    "CP1026");
        fIANA2JavaMap.put("IBM-1140",    "Cp1140");
        fIANA2JavaMap.put("IBM-1141",    "Cp1141");
        fIANA2JavaMap.put("IBM-1142",    "Cp1142");
        fIANA2JavaMap.put("IBM-1143",    "Cp1143");
        fIANA2JavaMap.put("IBM-1144",    "Cp1144");
        fIANA2JavaMap.put("IBM-1145",    "Cp1145");
        fIANA2JavaMap.put("IBM-1146",    "Cp1146");
        fIANA2JavaMap.put("IBM-1147",    "Cp1147");
        fIANA2JavaMap.put("IBM-1148",    "Cp1148");
        fIANA2JavaMap.put("IBM-1149",    "Cp1149");
        fIANA2JavaMap.put("IBM-819",      "ISO8859_1");
        fIANA2JavaMap.put("IBM-367",        "ASCII");

        // REVISIT:
        //   j:CNS11643 -> EUC-TW?
        //   ISO-2022-CN? ISO-2022-CN-EXT?
                                                
        // add Java to IANA encoding mappings
        //fJava2IANAMap.put("8859_1",    "US-ASCII"); // ?
        fJava2IANAMap.put("ISO8859_1",    "ISO-8859-1");
        fJava2IANAMap.put("ISO8859_2",    "ISO-8859-2");
        fJava2IANAMap.put("ISO8859_3",    "ISO-8859-3");
        fJava2IANAMap.put("ISO8859_4",    "ISO-8859-4");
        fJava2IANAMap.put("ISO8859_5",    "ISO-8859-5");
        fJava2IANAMap.put("ISO8859_6",    "ISO-8859-6");
        fJava2IANAMap.put("ISO8859_7",    "ISO-8859-7");
        fJava2IANAMap.put("ISO8859_8",    "ISO-8859-8");
        fJava2IANAMap.put("ISO8859_9",    "ISO-8859-9");
        fJava2IANAMap.put("Big5",      "BIG5");
        fJava2IANAMap.put("CP037",     "EBCDIC-CP-US");
        fJava2IANAMap.put("CP273",     "IBM273");
        fJava2IANAMap.put("CP277",     "EBCDIC-CP-DK");
        fJava2IANAMap.put("CP278",     "EBCDIC-CP-FI");
        fJava2IANAMap.put("CP280",     "EBCDIC-CP-IT");
        fJava2IANAMap.put("CP284",     "EBCDIC-CP-ES");
        fJava2IANAMap.put("CP285",     "EBCDIC-CP-GB");
        fJava2IANAMap.put("CP290",     "EBCDIC-JP-KANA");
        fJava2IANAMap.put("CP297",     "EBCDIC-CP-FR");
        fJava2IANAMap.put("CP420",     "EBCDIC-CP-AR1");
        fJava2IANAMap.put("CP424",     "EBCDIC-CP-HE");
        fJava2IANAMap.put("CP437",     "IBM437");
        fJava2IANAMap.put("CP500",     "EBCDIC-CP-CH");
        fJava2IANAMap.put("CP775",     "IBM775");
        fJava2IANAMap.put("CP850",     "IBM850");
        fJava2IANAMap.put("CP852",     "IBM852");
        fJava2IANAMap.put("CP855",     "IBM855");
        fJava2IANAMap.put("CP857",     "IBM857");
        fJava2IANAMap.put("CP858",     "IBM00858");
        fJava2IANAMap.put("CP860",     "IBM860");
        fJava2IANAMap.put("CP861",     "IBM861");
        fJava2IANAMap.put("CP862",     "IBM862");
        fJava2IANAMap.put("CP863",     "IBM863");
        fJava2IANAMap.put("CP864",     "IBM864");
        fJava2IANAMap.put("CP865",     "IBM865");
        fJava2IANAMap.put("CP866",     "IBM866");
        fJava2IANAMap.put("CP868",     "IBM868");
        fJava2IANAMap.put("CP869",     "IBM869");
        fJava2IANAMap.put("CP870",     "EBCDIC-CP-ROECE");
        fJava2IANAMap.put("CP871",     "EBCDIC-CP-IS");
        fJava2IANAMap.put("CP918",     "EBCDIC-CP-AR2");
        fJava2IANAMap.put("CP924",     "IBM00924");
        fJava2IANAMap.put("CP1026",     "IBM1026");
        fJava2IANAMap.put("Cp01140",     "IBM01140");
        fJava2IANAMap.put("Cp01141",     "IBM01141");
        fJava2IANAMap.put("Cp01142",     "IBM01142");
        fJava2IANAMap.put("Cp01143",     "IBM01143");
        fJava2IANAMap.put("Cp01144",     "IBM01144");
        fJava2IANAMap.put("Cp01145",     "IBM01145");
        fJava2IANAMap.put("Cp01146",     "IBM01146");
        fJava2IANAMap.put("Cp01147",     "IBM01147");
        fJava2IANAMap.put("Cp01148",     "IBM01148");
        fJava2IANAMap.put("Cp01149",     "IBM01149");
        fJava2IANAMap.put("EUCJIS",    "EUC-JP");
        fJava2IANAMap.put("GB2312",    "GB2312");
        fJava2IANAMap.put("ISO2022KR", "ISO-2022-KR");
        fJava2IANAMap.put("ISO2022CN", "ISO-2022-CN");
        fJava2IANAMap.put("JIS",       "ISO-2022-JP");
        fJava2IANAMap.put("KOI8_R",    "KOI8-R");
        fJava2IANAMap.put("KSC5601",   "EUC-KR");
        fJava2IANAMap.put("GB18030",      "GB18030");
        fJava2IANAMap.put("SJIS",      "SHIFT_JIS");
        fJava2IANAMap.put("MS932",      "WINDOWS-31J");
        fJava2IANAMap.put("UTF8",      "UTF-8");
        fJava2IANAMap.put("Unicode",   "UTF-16");
        fJava2IANAMap.put("UnicodeBig",   "UTF-16BE");
        fJava2IANAMap.put("UnicodeLittle",   "UTF-16LE");
        fJava2IANAMap.put("JIS0201",  "X0201");
        fJava2IANAMap.put("JIS0208",  "X0208");
        fJava2IANAMap.put("JIS0212",  "ISO-IR-159");

        // proposed addition (see above for details):
        fJava2IANAMap.put("CP1047",    "IBM1047");

    } // <clinit>()

    //
    // Constructors
    //

    /** Default constructor. */
    public EncodingMap() 
    {
      super();
    }

    //
    // Public static methods
    //

    /**
     * Adds an IANA to Java encoding name mapping.
     * 
     * @param ianaEncoding The IANA encoding name.
     * @param javaEncoding The Java encoding name.
     */
    public static void putIANA2JavaMapping(String ianaEncoding, 
                                           String javaEncoding) {
        fIANA2JavaMap.put(ianaEncoding, javaEncoding);
    } // putIANA2JavaMapping(String,String)

    /**
     * Returns the Java encoding name for the specified IANA encoding name.
     * 
     * @param ianaEncoding The IANA encoding name.
     */
    public static String getIANA2JavaMapping(String ianaEncoding) {
        return fIANA2JavaMap.get(ianaEncoding);
    } // getIANA2JavaMapping(String):String

    /**
     * Removes an IANA to Java encoding name mapping.
     * 
     * @param ianaEncoding The IANA encoding name.
     */
    public static String removeIANA2JavaMapping(String ianaEncoding) {
        return fIANA2JavaMap.remove(ianaEncoding);
    } // removeIANA2JavaMapping(String):String

    /**
     * Adds a Java to IANA encoding name mapping.
     * 
     * @param javaEncoding The Java encoding name.
     * @param ianaEncoding The IANA encoding name.
     */
    public static void putJava2IANAMapping(String javaEncoding, 
                                           String ianaEncoding) {
        fJava2IANAMap.put(javaEncoding, ianaEncoding);
    } // putJava2IANAMapping(String,String)

    /**
     * Returns the IANA encoding name for the specified Java encoding name.
     * 
     * @param javaEncoding The Java encoding name.
     */
    public static String getJava2IANAMapping(String javaEncoding) {
        return fJava2IANAMap.get(javaEncoding);
    } // getJava2IANAMapping(String):String

    /**
     * Removes a Java to IANA encoding name mapping.
     * 
     * @param javaEncoding The Java encoding name.
     */
    public static String removeJava2IANAMapping(String javaEncoding) {
        return fJava2IANAMap.remove(javaEncoding);
    } // removeJava2IANAMapping

} // class EncodingMap


/**********************************************************************
* A class to represent a Uniform Resource Identifier (URI). This class
* is designed to handle the parsing of URIs and provide access to
* the various components (scheme, host, port, userinfo, path, query
* string and fragment) that may constitute a URI.
* <p>
* Parsing of a URI specification is done according to the URI
* syntax described in 
* <a href="http://www.ietf.org/rfc/rfc2396.txt?number=2396">RFC 2396</a>,
* and amended by
* <a href="http://www.ietf.org/rfc/rfc2732.txt?number=2732">RFC 2732</a>. 
* <p>
* Every absolute URI consists of a scheme, followed by a colon (':'), 
* followed by a scheme-specific part. For URIs that follow the 
* "generic URI" syntax, the scheme-specific part begins with two 
* slashes ("//") and may be followed by an authority segment (comprised 
* of user information, host, and port), path segment, query segment 
* and fragment. Note that RFC 2396 no longer specifies the use of the 
* parameters segment and excludes the "user:password" syntax as part of 
* the authority segment. If "user:password" appears in a URI, the entire 
* user/password string is stored as userinfo.
* <p>
* For URIs that do not follow the "generic URI" syntax (e.g. mailto),
* the entire scheme-specific part is treated as the "path" portion
* of the URI.
* <p>
* Note that, unlike the java.net.URL class, this class does not provide
* any built-in network access functionality nor does it provide any
* scheme-specific functionality (for example, it does not know a
* default port for a specific scheme). Rather, it only knows the
* grammar and basic set of operations that can be applied to a URI.
*
**********************************************************************/
 public static final class URI implements Serializable {

  private static final long serialVersionUID = 1L;

  /*******************************************************************
  * MalformedURIExceptions are thrown in the process of building a URI
  * or setting fields on a URI when an operation would result in an
  * invalid URI specification.
  *
  ********************************************************************/
  public static class MalformedURIException extends IOException {
    
    private static final long serialVersionUID = 1L;

   /******************************************************************
    * Constructs a <code>MalformedURIException</code> with no specified
    * detail message.
    ******************************************************************/
    public MalformedURIException() {
      super();
    }

    /*****************************************************************
    * Constructs a <code>MalformedURIException</code> with the
    * specified detail message.
    *
    * @param p_msg the detail message.
    ******************************************************************/
    public MalformedURIException(String p_msg) {
      super(p_msg);
    }
  }

  private static final byte [] fgLookupTable = new byte[128];
  
  /**
   * Character Classes
   */
  
  /** reserved characters ;/?:@&=+$,[] */
  //RFC 2732 added '[' and ']' as reserved characters
  private static final int RESERVED_CHARACTERS = 0x01;
  
  /** URI punctuation mark characters: -_.!~*'() - these, combined with
      alphanumerics, constitute the "unreserved" characters */
  private static final int MARK_CHARACTERS = 0x02;
  
  /** scheme can be composed of alphanumerics and these characters: +-. */
  private static final int SCHEME_CHARACTERS = 0x04;
  
  /** userinfo can be composed of unreserved, escaped and these
      characters: ;:&=+$, */
  private static final int USERINFO_CHARACTERS = 0x08;
  
  /** ASCII letter characters */
  private static final int ASCII_ALPHA_CHARACTERS = 0x10;
  
  /** ASCII digit characters */
  private static final int ASCII_DIGIT_CHARACTERS = 0x20;
  
  /** ASCII hex characters */
  private static final int ASCII_HEX_CHARACTERS = 0x40;
  
  /** Path characters */
  private static final int PATH_CHARACTERS = 0x80;

  /** Mask for alpha-numeric characters */
  private static final int MASK_ALPHA_NUMERIC = ASCII_ALPHA_CHARACTERS | ASCII_DIGIT_CHARACTERS;
  
  /** Mask for unreserved characters */
  private static final int MASK_UNRESERVED_MASK = MASK_ALPHA_NUMERIC | MARK_CHARACTERS;
  
  /** Mask for URI allowable characters except for % */
  private static final int MASK_URI_CHARACTER = MASK_UNRESERVED_MASK | RESERVED_CHARACTERS;
  
  /** Mask for scheme characters */
  private static final int MASK_SCHEME_CHARACTER = MASK_ALPHA_NUMERIC | SCHEME_CHARACTERS;
  
  /** Mask for userinfo characters */
  private static final int MASK_USERINFO_CHARACTER = MASK_UNRESERVED_MASK | USERINFO_CHARACTERS;
  
  /** Mask for path characters */
  private static final int MASK_PATH_CHARACTER = MASK_UNRESERVED_MASK | PATH_CHARACTERS; 

  static {
      // Add ASCII Digits and ASCII Hex Numbers
      for (int i = '0'; i <= '9'; ++i) {
          fgLookupTable[i] |= ASCII_DIGIT_CHARACTERS | ASCII_HEX_CHARACTERS;
      }

      // Add ASCII Letters and ASCII Hex Numbers
      for (int i = 'A'; i <= 'F'; ++i) {
          fgLookupTable[i] |= ASCII_ALPHA_CHARACTERS | ASCII_HEX_CHARACTERS;
          fgLookupTable[i+0x00000020] |= ASCII_ALPHA_CHARACTERS | ASCII_HEX_CHARACTERS;
      }

      // Add ASCII Letters
      for (int i = 'G'; i <= 'Z'; ++i) {
          fgLookupTable[i] |= ASCII_ALPHA_CHARACTERS;
          fgLookupTable[i+0x00000020] |= ASCII_ALPHA_CHARACTERS;
      }

      // Add Reserved Characters
      fgLookupTable[';'] |= RESERVED_CHARACTERS;
      fgLookupTable['/'] |= RESERVED_CHARACTERS;
      fgLookupTable['?'] |= RESERVED_CHARACTERS;
      fgLookupTable[':'] |= RESERVED_CHARACTERS;
      fgLookupTable['@'] |= RESERVED_CHARACTERS;
      fgLookupTable['&'] |= RESERVED_CHARACTERS;
      fgLookupTable['='] |= RESERVED_CHARACTERS;
      fgLookupTable['+'] |= RESERVED_CHARACTERS;
      fgLookupTable['$'] |= RESERVED_CHARACTERS;
      fgLookupTable[','] |= RESERVED_CHARACTERS;
      fgLookupTable['['] |= RESERVED_CHARACTERS;
      fgLookupTable[']'] |= RESERVED_CHARACTERS;

      // Add Mark Characters
      fgLookupTable['-'] |= MARK_CHARACTERS;
      fgLookupTable['_'] |= MARK_CHARACTERS;
      fgLookupTable['.'] |= MARK_CHARACTERS;
      fgLookupTable['!'] |= MARK_CHARACTERS;
      fgLookupTable['~'] |= MARK_CHARACTERS;
      fgLookupTable['*'] |= MARK_CHARACTERS;
      fgLookupTable['\''] |= MARK_CHARACTERS;
      fgLookupTable['('] |= MARK_CHARACTERS;
      fgLookupTable[')'] |= MARK_CHARACTERS;

      // Add Scheme Characters
      fgLookupTable['+'] |= SCHEME_CHARACTERS;
      fgLookupTable['-'] |= SCHEME_CHARACTERS;
      fgLookupTable['.'] |= SCHEME_CHARACTERS;

      // Add Userinfo Characters
      fgLookupTable[';'] |= USERINFO_CHARACTERS;
      fgLookupTable[':'] |= USERINFO_CHARACTERS;
      fgLookupTable['&'] |= USERINFO_CHARACTERS;
      fgLookupTable['='] |= USERINFO_CHARACTERS;
      fgLookupTable['+'] |= USERINFO_CHARACTERS;
      fgLookupTable['$'] |= USERINFO_CHARACTERS;
      fgLookupTable[','] |= USERINFO_CHARACTERS;
      
      // Add Path Characters
      fgLookupTable[';'] |= PATH_CHARACTERS;
      fgLookupTable['/'] |= PATH_CHARACTERS;
      fgLookupTable[':'] |= PATH_CHARACTERS;
      fgLookupTable['@'] |= PATH_CHARACTERS;
      fgLookupTable['&'] |= PATH_CHARACTERS;
      fgLookupTable['='] |= PATH_CHARACTERS;
      fgLookupTable['+'] |= PATH_CHARACTERS;
      fgLookupTable['$'] |= PATH_CHARACTERS;
      fgLookupTable[','] |= PATH_CHARACTERS;
  }
  public static final URI BASE_URI;
  static {
    URI uri = null;
    try {
        uri = new URI("abc://def.ghi.jkl");
    } catch (URI.MalformedURIException ex) {
      // Just use null.
    }
    BASE_URI = uri;
  }
  /** Stores the scheme (usually the protocol) for this URI. */
  private String m_scheme = null;

  /** If specified, stores the userinfo for this URI; otherwise null */
  private String m_userinfo = null;

  /** If specified, stores the host for this URI; otherwise null */
  private String m_host = null;

  /** If specified, stores the port for this URI; otherwise -1 */
  private int m_port = -1;
  
  /** If specified, stores the registry based authority for this URI; otherwise -1 */
  private String m_regAuthority = null;

  /** If specified, stores the path for this URI; otherwise null */
  private String m_path = null;

  /** If specified, stores the query string for this URI; otherwise
      null.  */
  private String m_queryString = null;

  /** If specified, stores the fragment for this URI; otherwise null */
  private String m_fragment = null;

  /**
  * Construct a new and uninitialized URI.
  */
  public URI() {
    super();
  }

 /**
  * Construct a new URI from another URI. All fields for this URI are
  * set equal to the fields of the URI passed in.
  *
  * @param p_other the URI to copy (cannot be null)
  */
  public URI(URI p_other) {
    initialize(p_other);
  }

 /**
  * Construct a new URI from a URI specification string. If the
  * specification follows the "generic URI" syntax, (two slashes
  * following the first colon), the specification will be parsed
  * accordingly - setting the scheme, userinfo, host,port, path, query
  * string and fragment fields as necessary. If the specification does
  * not follow the "generic URI" syntax, the specification is parsed
  * into a scheme and scheme-specific part (stored as the path) only.
  *
  * @param p_uriSpec the URI specification string (cannot be null or
  *                  empty)
  *
  * @exception MalformedURIException if p_uriSpec violates any syntax
  *                                   rules
  */
  public URI(String p_uriSpec) throws MalformedURIException {
    this((URI)null, p_uriSpec);
  }

 /**
  * Construct a new URI from a base URI and a URI specification string.
  * The URI specification string may be a relative URI.
  *
  * @param p_base the base URI (cannot be null if p_uriSpec is null or
  *               empty)
  * @param p_uriSpec the URI specification string (cannot be null or
  *                  empty if p_base is null)
  *
  * @exception MalformedURIException if p_uriSpec violates any syntax
  *                                  rules
  */
  public URI(URI p_base, String p_uriSpec) throws MalformedURIException {
    initialize(p_base, p_uriSpec);
  }

 /**
  * Construct a new URI that does not follow the generic URI syntax.
  * Only the scheme and scheme-specific part (stored as the path) are
  * initialized.
  *
  * @param p_scheme the URI scheme (cannot be null or empty)
  * @param p_schemeSpecificPart the scheme-specific part (cannot be
  *                             null or empty)
  *
  * @exception MalformedURIException if p_scheme violates any
  *                                  syntax rules
  */
  public URI(String p_scheme, String p_schemeSpecificPart)
             throws MalformedURIException {
    if (p_scheme == null || p_scheme.trim().length() == 0) {
      throw new MalformedURIException(
            "Cannot construct URI with null/empty scheme!");
    }
    if (p_schemeSpecificPart == null ||
        p_schemeSpecificPart.trim().length() == 0) {
      throw new MalformedURIException(
          "Cannot construct URI with null/empty scheme-specific part!");
    }
    setScheme(p_scheme);
    setPath(p_schemeSpecificPart);
  }

 /**
  * Construct a new URI that follows the generic URI syntax from its
  * component parts. Each component is validated for syntax and some
  * basic semantic checks are performed as well.  See the individual
  * setter methods for specifics.
  *
  * @param p_scheme the URI scheme (cannot be null or empty)
  * @param p_host the hostname, IPv4 address or IPv6 reference for the URI
  * @param p_path the URI path - if the path contains '?' or '#',
  *               then the query string and/or fragment will be
  *               set from the path; however, if the query and
  *               fragment are specified both in the path and as
  *               separate parameters, an exception is thrown
  * @param p_queryString the URI query string (cannot be specified
  *                      if path is null)
  * @param p_fragment the URI fragment (cannot be specified if path
  *                   is null)
  *
  * @exception MalformedURIException if any of the parameters violates
  *                                  syntax rules or semantic rules
  */
  public URI(String p_scheme, String p_host, String p_path,
             String p_queryString, String p_fragment)
         throws MalformedURIException {
    this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
  }

 /**
  * Construct a new URI that follows the generic URI syntax from its
  * component parts. Each component is validated for syntax and some
  * basic semantic checks are performed as well.  See the individual
  * setter methods for specifics.
  *
  * @param p_scheme the URI scheme (cannot be null or empty)
  * @param p_userinfo the URI userinfo (cannot be specified if host
  *                   is null)
  * @param p_host the hostname, IPv4 address or IPv6 reference for the URI
  * @param p_port the URI port (may be -1 for "unspecified"; cannot
  *               be specified if host is null)
  * @param p_path the URI path - if the path contains '?' or '#',
  *               then the query string and/or fragment will be
  *               set from the path; however, if the query and
  *               fragment are specified both in the path and as
  *               separate parameters, an exception is thrown
  * @param p_queryString the URI query string (cannot be specified
  *                      if path is null)
  * @param p_fragment the URI fragment (cannot be specified if path
  *                   is null)
  *
  * @exception MalformedURIException if any of the parameters violates
  *                                  syntax rules or semantic rules
  */
  public URI(String p_scheme, String p_userinfo,
             String p_host, int p_port, String p_path,
             String p_queryString, String p_fragment)
         throws MalformedURIException {
    if (p_scheme == null || p_scheme.trim().length() == 0) {
      throw new MalformedURIException("Scheme is required!");
    }

    if (p_host == null) {
      if (p_userinfo != null) {
        throw new MalformedURIException(
             "Userinfo may not be specified if host is not specified!");
      }
      if (p_port != -1) {
        throw new MalformedURIException(
             "Port may not be specified if host is not specified!");
      }
    }

    if (p_path != null) {
      if (p_path.indexOf('?') != -1 && p_queryString != null) {
        throw new MalformedURIException(
          "Query string cannot be specified in path and query string!");
      }

      if (p_path.indexOf('#') != -1 && p_fragment != null) {
        throw new MalformedURIException(
          "Fragment cannot be specified in both the path and fragment!");
      }
    }

    setScheme(p_scheme);
    setHost(p_host);
    setPort(p_port);
    setUserinfo(p_userinfo);
    setPath(p_path);
    setQueryString(p_queryString);
    setFragment(p_fragment);
  }

 /**
  * Initialize all fields of this URI from another URI.
  *
  * @param p_other the URI to copy (cannot be null)
  */
  private void initialize(URI p_other) {
    m_scheme = p_other.getScheme();
    m_userinfo = p_other.getUserinfo();
    m_host = p_other.getHost();
    m_port = p_other.getPort();
    m_regAuthority = p_other.getRegBasedAuthority();
    m_path = p_other.getPath();
    m_queryString = p_other.getQueryString();
    m_fragment = p_other.getFragment();
  }

 /**
  * Initializes this URI from a base URI and a URI specification string.
  * See RFC 2396 Section 4 and Appendix B for specifications on parsing
  * the URI and Section 5 for specifications on resolving relative URIs
  * and relative paths.
  *
  * @param p_base the base URI (may be null if p_uriSpec is an absolute
  *               URI)
  * @param p_uriSpec the URI spec string which may be an absolute or
  *                  relative URI (can only be null/empty if p_base
  *                  is not null)
  *
  * @exception MalformedURIException if p_base is null and p_uriSpec
  *                                  is not an absolute URI or if
  *                                  p_uriSpec violates syntax rules
  */
  private void initialize(URI p_base, String p_uriSpec)
                         throws MalformedURIException {
    
    String uriSpec = p_uriSpec;
    int uriSpecLen = (uriSpec != null) ? uriSpec.length() : 0;
  
    if (p_base == null && uriSpecLen == 0) {
      throw new MalformedURIException(
                  "Cannot initialize URI with empty parameters.");
    }

    // just make a copy of the base if spec is empty
    if (uriSpecLen == 0) {
      initialize(p_base);
      return;
    }

    int index = 0;

    // Check for scheme, which must be before '/', '?' or '#'. Also handle
    // names with DOS drive letters ('D:'), so 1-character schemes are not
    // allowed.
    @SuppressWarnings("null")
    int colonIdx = uriSpec.indexOf(':');
    if (colonIdx != -1) {
        final int searchFrom = colonIdx - 1;
        // search backwards starting from character before ':'.
        int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
        int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
        int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);
       
        if (colonIdx < 2 || slashIdx != -1 || 
            queryIdx != -1 || fragmentIdx != -1) {
            // A standalone base is a valid URI according to spec
            if (colonIdx == 0 || (p_base == null && fragmentIdx != 0)) {
                throw new MalformedURIException("No scheme found in URI.");
            }
        }
        else {
            initializeScheme(uriSpec);
            index = m_scheme.length()+1;
            
            // Neither 'scheme:' or 'scheme:#fragment' are valid URIs.
            if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx+1) == '#') {
              throw new MalformedURIException("Scheme specific part cannot be empty."); 
            }
        }
    }
    else if (p_base == null && uriSpec.indexOf('#') != 0) {
        throw new MalformedURIException("No scheme found in URI.");    
    }

    // Two slashes means we may have authority, but definitely means we're either
    // matching net_path or abs_path. These two productions are ambiguous in that
    // every net_path (except those containing an IPv6Reference) is an abs_path. 
    // RFC 2396 resolves this ambiguity by applying a greedy left most matching rule. 
    // Try matching net_path first, and if that fails we don't have authority so 
    // then attempt to match abs_path.
    //
    // net_path = "//" authority [ abs_path ]
    // abs_path = "/"  path_segments
    if (((index+1) < uriSpecLen) &&
        (uriSpec.charAt(index) == '/' && uriSpec.charAt(index+1) == '/')) {
      index += 2;
      int startPos = index;

      // Authority will be everything up to path, query or fragment
      char testChar = '\0';
      while (index < uriSpecLen) {
        testChar = uriSpec.charAt(index);
        if (testChar == '/' || testChar == '?' || testChar == '#') {
          break;
        }
        index++;
      }

      // Attempt to parse authority. If the section is an empty string
      // this is a valid server based authority, so set the host to this
      // value.
      if (index > startPos) {
        // If we didn't find authority we need to back up. Attempt to
        // match against abs_path next.
        if (!initializeAuthority(uriSpec.substring(startPos, index))) {
          index = startPos - 2;
        }
      }
      else {
        m_host = "";
      }
    }

    initializePath(uriSpec, index);

    // Resolve relative URI to base URI - see RFC 2396 Section 5.2
    // In some cases, it might make more sense to throw an exception
    // (when scheme is specified is the string spec and the base URI
    // is also specified, for example), but we're just following the
    // RFC specifications
    if (p_base != null) {

      // check to see if this is the current doc - RFC 2396 5.2 #2
      // note that this is slightly different from the RFC spec in that
      // we don't include the check for query string being null
      // - this handles cases where the urispec is just a query
      // string or a fragment (e.g. "?y" or "#s") -
      // see <http://www.ics.uci.edu/~fielding/url/test1.html> which
      // identified this as a bug in the RFC
      if (m_path.length() == 0 && m_scheme == null &&
          m_host == null && m_regAuthority == null) {
        m_scheme = p_base.getScheme();
        m_userinfo = p_base.getUserinfo();
        m_host = p_base.getHost();
        m_port = p_base.getPort();
        m_regAuthority = p_base.getRegBasedAuthority();
        m_path = p_base.getPath();

        if (m_queryString == null) {
          m_queryString = p_base.getQueryString();
        }
        return;
      }

      // check for scheme - RFC 2396 5.2 #3
      // if we found a scheme, it means absolute URI, so we're done
      if (m_scheme == null) {
        m_scheme = p_base.getScheme();
      }
      else {
        return;
      }

      // check for authority - RFC 2396 5.2 #4
      // if we found a host, then we've got a network path, so we're done
      if (m_host == null && m_regAuthority == null) {
        m_userinfo = p_base.getUserinfo();
        m_host = p_base.getHost();
        m_port = p_base.getPort();
        m_regAuthority = p_base.getRegBasedAuthority();
      }
      else {
        return;
      }

      // check for absolute path - RFC 2396 5.2 #5
      if (m_path.length() > 0 &&
          m_path.startsWith("/")) {
        return;
      }

      // if we get to this point, we need to resolve relative path
      // RFC 2396 5.2 #6
      String path = "";
      String basePath = p_base.getPath();

      // 6a - get all but the last segment of the base URI path
      if (basePath != null && basePath.length() > 0) {
        int lastSlash = basePath.lastIndexOf('/');
        if (lastSlash != -1) {
          path = basePath.substring(0, lastSlash+1);
        }
      }
      else if (m_path.length() > 0) {
        path = "/";
      }

      // 6b - append the relative URI path
      path = path.concat(m_path);

      // 6c - remove all "./" where "." is a complete path segment
      index = -1;
      while ((index = path.indexOf("/./")) != -1) {
        path = path.substring(0, index+1).concat(path.substring(index+3));
      }

      // 6d - remove "." if path ends with "." as a complete path segment
      if (path.endsWith("/.")) {
        path = path.substring(0, path.length()-1);
      }

      // 6e - remove all "<segment>/../" where "<segment>" is a complete
      // path segment not equal to ".."
      index = 1;
      int segIndex = -1;
      String tempString = null;

      while ((index = path.indexOf("/../", index)) > 0) {
        tempString = path.substring(0, path.indexOf("/../"));
        segIndex = tempString.lastIndexOf('/');
        if (segIndex != -1) {
          if (!tempString.substring(segIndex).equals("..")) {
            path = path.substring(0, segIndex+1).concat(path.substring(index+4));
            index = segIndex;
          }
          else
            index += 4;
        }
        else
          index += 4;
      }

      // 6f - remove ending "<segment>/.." where "<segment>" is a
      // complete path segment
      if (path.endsWith("/..")) {
        tempString = path.substring(0, path.length()-3);
        segIndex = tempString.lastIndexOf('/');
        if (segIndex != -1) {
          path = path.substring(0, segIndex+1);
        }
      }
      m_path = path;
    }
  }

 /**
  * Initialize the scheme for this URI from a URI string spec.
  *
  * @param p_uriSpec the URI specification (cannot be null)
  *
  * @exception MalformedURIException if URI does not have a conformant
  *                                  scheme
  */
  private void initializeScheme(String p_uriSpec)
                 throws MalformedURIException {
    int uriSpecLen = p_uriSpec.length();
    int index = 0;
    String scheme = null;
    char testChar = '\0';

    while (index < uriSpecLen) {
      testChar = p_uriSpec.charAt(index);
      if (testChar == ':' || testChar == '/' ||
          testChar == '?' || testChar == '#') {
        break;
      }
      index++;
    }
    scheme = p_uriSpec.substring(0, index);

    if (scheme.length() == 0) {
      throw new MalformedURIException("No scheme found in URI.");
    }
    else {
      setScheme(scheme);
    }
  }

 /**
  * Initialize the authority (either server or registry based)
  * for this URI from a URI string spec.
  *
  * @param p_uriSpec the URI specification (cannot be null)
  * 
  * @return true if the given string matched server or registry
  * based authority
  */
  private boolean initializeAuthority(String p_uriSpec) {
    
    int index = 0;
    int start = 0;
    int end = p_uriSpec.length();

    char testChar = '\0';
    String userinfo = null;

    // userinfo is everything up to @
    if (p_uriSpec.indexOf('@', start) != -1) {
      while (index < end) {
        testChar = p_uriSpec.charAt(index);
        if (testChar == '@') {
          break;
        }
        index++;
      }
      userinfo = p_uriSpec.substring(start, index);
      index++;
    }

    // host is everything up to last ':', or up to 
    // and including ']' if followed by ':'.
    String host = null;
    start = index;
    boolean hasPort = false;
    if (index < end) {
      if (p_uriSpec.charAt(start) == '[') {
        int bracketIndex = p_uriSpec.indexOf(']', start);
        index = (bracketIndex != -1) ? bracketIndex : end;
        if (index+1 < end && p_uriSpec.charAt(index+1) == ':') {
          ++index;
          hasPort = true;
        }
        else {
          index = end;
        }
      }
      else {
        int colonIndex = p_uriSpec.lastIndexOf(':', end);
        index = (colonIndex > start) ? colonIndex : end;
        hasPort = (index != end);
      }
    }
    host = p_uriSpec.substring(start, index);
    int port = -1;
    if (host.length() > 0) {
      // port
      if (hasPort) {
        index++;
        start = index;
        while (index < end) {
          index++;
        }
        String portStr = p_uriSpec.substring(start, index);
        if (portStr.length() > 0) {
          // REVISIT: Remove this code.
          /** for (int i = 0; i < portStr.length(); i++) {
            if (!isDigit(portStr.charAt(i))) {
              throw new MalformedURIException(
                   portStr +
                   " is invalid. Port should only contain digits!");
            }
          }**/
          // REVISIT: Remove this code.
          // Store port value as string instead of integer.
          try {
            port = Integer.parseInt(portStr);
            if (port == -1) --port;
          }
          catch (NumberFormatException nfe) {
            port = -2;
          }
        }
      }
    }
    
    if (isValidServerBasedAuthority(host, port, userinfo)) {
      m_host = host;
      m_port = port;
      m_userinfo = userinfo;
      return true;
    }
    // Note: Registry based authority is being removed from a
    // new spec for URI which would obsolete RFC 2396. If the
    // spec is added to XML errata, processing of reg_name
    // needs to be removed. - mrglavas.
    else if (isValidRegistryBasedAuthority(p_uriSpec)) {
      m_regAuthority = p_uriSpec;
      return true;
    }
    return false;
  }
  
  /**
   * Determines whether the components host, port, and user info
   * are valid as a server authority.
   * 
   * @param host the host component of authority
   * @param port the port number component of authority
   * @param userinfo the user info component of authority
   * 
   * @return true if the given host, port, and userinfo compose
   * a valid server authority
   */
  private boolean isValidServerBasedAuthority(String host, int port, String userinfo) {
    
    // Check if the host is well formed.
    if (!isWellFormedAddress(host)) {
      return false;
    }
    
    // Check that port is well formed if it exists.
    // REVISIT: There's no restriction on port value ranges, but
    // perform the same check as in setPort to be consistent. Pass
    // in a string to this method instead of an integer.
    if (port < -1 || port > 65535) {
      return false;
    }
    
    // Check that userinfo is well formed if it exists.
    if (userinfo != null) {
      // Userinfo can contain alphanumerics, mark characters, escaped
      // and ';',':','&','=','+','$',','
      int index = 0;
      int end = userinfo.length();
      char testChar = '\0';
      while (index < end) {
        testChar = userinfo.charAt(index);
        if (testChar == '%') {
          if (index+2 >= end ||
            !isHex(userinfo.charAt(index+1)) ||
            !isHex(userinfo.charAt(index+2))) {
            return false;
          }
          index += 2;
        }
        else if (!isUserinfoCharacter(testChar)) {
          return false;
        }
        ++index;
      }
    }
    return true;
  }
  
  /**
   * Determines whether the given string is a registry based authority.
   * 
   * @param authority the authority component of a URI
   * 
   * @return true if the given string is a registry based authority
   */
  private boolean isValidRegistryBasedAuthority(String authority) {
    int index = 0;
    int end = authority.length();
    char testChar;
    
    while (index < end) {
      testChar = authority.charAt(index);
      
      // check for valid escape sequence
      if (testChar == '%') {
        if (index+2 >= end ||
            !isHex(authority.charAt(index+1)) ||
            !isHex(authority.charAt(index+2))) {
            return false;
        }
        index += 2;
      }
      // can check against path characters because the set
      // is the same except for '/' which we've already excluded.
      else if (!isPathCharacter(testChar)) {
        return false;
      }
      ++index;
    }
    return true;
  }
    
 /**
  * Initialize the path for this URI from a URI string spec.
  *
  * @param p_uriSpec the URI specification (cannot be null)
  * @param p_nStartIndex the index to begin scanning from
  *
  * @exception MalformedURIException if p_uriSpec violates syntax rules
  */
  private void initializePath(String p_uriSpec, int p_nStartIndex)
                 throws MalformedURIException {
    if (p_uriSpec == null) {
      throw new MalformedURIException(
                "Cannot initialize path from null string!");
    }

    int index = p_nStartIndex;
    int start = p_nStartIndex;
    int end = p_uriSpec.length();
    char testChar = '\0';

    // path - everything up to query string or fragment
    if (start < end) {
      // RFC 2732 only allows '[' and ']' to appear in the opaque part.
      if (getScheme() == null || p_uriSpec.charAt(start) == '/') {
      
            // Scan path.
            // abs_path = "/"  path_segments
            // rel_path = rel_segment [ abs_path ]
            while (index < end) {
                testChar = p_uriSpec.charAt(index);
            
                // check for valid escape sequence
                if (testChar == '%') {
                    if (index+2 >= end ||
                    !isHex(p_uriSpec.charAt(index+1)) ||
                    !isHex(p_uriSpec.charAt(index+2))) {
                        throw new MalformedURIException(
                            "Path contains invalid escape sequence!");
                    }
                    index += 2;
                }
                // Path segments cannot contain '[' or ']' since pchar
                // production was not changed by RFC 2732.
                else if (!isPathCharacter(testChar)) {
                    if (testChar == '?' || testChar == '#') {
                        break;
                    }
                    throw new MalformedURIException(
                        "Path contains invalid character: " + testChar);
                }
                ++index;
            }
        }
        else {
            
            // Scan opaque part.
            // opaque_part = uric_no_slash *uric
            while (index < end) {
                testChar = p_uriSpec.charAt(index);
            
                if (testChar == '?' || testChar == '#') {
                    break;
                }
                
                // check for valid escape sequence
                if (testChar == '%') {
                    if (index+2 >= end ||
                    !isHex(p_uriSpec.charAt(index+1)) ||
                    !isHex(p_uriSpec.charAt(index+2))) {
                        throw new MalformedURIException(
                            "Opaque part contains invalid escape sequence!");
                    }
                    index += 2;
                }
                // If the scheme specific part is opaque, it can contain '['
                // and ']'. uric_no_slash wasn't modified by RFC 2732, which
                // I've interpreted as an error in the spec, since the 
                // production should be equivalent to (uric - '/'), and uric
                // contains '[' and ']'. - mrglavas
                else if (!isURICharacter(testChar)) {
                    throw new MalformedURIException(
                        "Opaque part contains invalid character: " + testChar);
                }
                ++index;
            }
        }
    }
    m_path = p_uriSpec.substring(start, index);

    // query - starts with ? and up to fragment or end
    if (testChar == '?') {
      index++;
      start = index;
      while (index < end) {
        testChar = p_uriSpec.charAt(index);
        if (testChar == '#') {
          break;
        }
        if (testChar == '%') {
           if (index+2 >= end ||
              !isHex(p_uriSpec.charAt(index+1)) ||
              !isHex(p_uriSpec.charAt(index+2))) {
            throw new MalformedURIException(
                    "Query string contains invalid escape sequence!");
           }
           index += 2;
        }
        else if (!isURICharacter(testChar)) {
          throw new MalformedURIException(
                "Query string contains invalid character: " + testChar);
        }
        index++;
      }
      m_queryString = p_uriSpec.substring(start, index);
    }

    // fragment - starts with #
    if (testChar == '#') {
      index++;
      start = index;
      while (index < end) {
        testChar = p_uriSpec.charAt(index);

        if (testChar == '%') {
           if (index+2 >= end ||
              !isHex(p_uriSpec.charAt(index+1)) ||
              !isHex(p_uriSpec.charAt(index+2))) {
            throw new MalformedURIException(
                    "Fragment contains invalid escape sequence!");
           }
           index += 2;
        }
        else if (!isURICharacter(testChar)) {
          throw new MalformedURIException(
                "Fragment contains invalid character: "+testChar);
        }
        index++;
      }
      m_fragment = p_uriSpec.substring(start, index);
    }
  }

 /**
  * Get the scheme for this URI.
  *
  * @return the scheme for this URI
  */
  public String getScheme() {
    return m_scheme;
  }

 /**
  * Get the scheme-specific part for this URI (everything following the
  * scheme and the first colon). See RFC 2396 Section 5.2 for spec.
  *
  * @return the scheme-specific part for this URI
  */
  public String getSchemeSpecificPart() {
    StringBuffer schemespec = new StringBuffer();

    if (m_host != null || m_regAuthority != null) {
      schemespec.append("//");
    
      // Server based authority.
      if (m_host != null) {

        if (m_userinfo != null) {
          schemespec.append(m_userinfo);
          schemespec.append('@');
        }
        
        schemespec.append(m_host);
        
        if (m_port != -1) {
          schemespec.append(':');
          schemespec.append(m_port);
        }
      }
      // Registry based authority.
      else {
        schemespec.append(m_regAuthority);
      }
    }

    if (m_path != null) {
      schemespec.append((m_path));
    }

    if (m_queryString != null) {
      schemespec.append('?');
      schemespec.append(m_queryString);
    }

    if (m_fragment != null) {
      schemespec.append('#');
      schemespec.append(m_fragment);
    }

    return schemespec.toString();
  }

 /**
  * Get the userinfo for this URI.
  *
  * @return the userinfo for this URI (null if not specified).
  */
  public String getUserinfo() {
    return m_userinfo;
  }

  /**
  * Get the host for this URI.
  *
  * @return the host for this URI (null if not specified).
  */
  public String getHost() {
    return m_host;
  }

 /**
  * Get the port for this URI.
  *
  * @return the port for this URI (-1 if not specified).
  */
  public int getPort() {
    return m_port;
  }
  
  /**
   * Get the registry based authority for this URI.
   * 
   * @return the registry based authority (null if not specified).
   */
  public String getRegBasedAuthority() {
    return m_regAuthority;
  }

 /**
  * Get the path for this URI (optionally with the query string and
  * fragment).
  *
  * @param p_includeQueryString if true (and query string is not null),
  *                             then a "?" followed by the query string
  *                             will be appended
  * @param p_includeFragment if true (and fragment is not null),
  *                             then a "#" followed by the fragment
  *                             will be appended
  *
  * @return the path for this URI possibly including the query string
  *         and fragment
  */
  public String getPath(boolean p_includeQueryString,
                        boolean p_includeFragment) {
    StringBuffer pathString = new StringBuffer(m_path);

    if (p_includeQueryString && m_queryString != null) {
      pathString.append('?');
      pathString.append(m_queryString);
    }

    if (p_includeFragment && m_fragment != null) {
      pathString.append('#');
      pathString.append(m_fragment);
    }
    return pathString.toString();
  }

 /**
  * Get the path for this URI. Note that the value returned is the path
  * only and does not include the query string or fragment.
  *
  * @return the path for this URI.
  */
  public String getPath() {
    return m_path;
  }

 /**
  * Get the query string for this URI.
  *
  * @return the query string for this URI. Null is returned if there
  *         was no "?" in the URI spec, empty string if there was a
  *         "?" but no query string following it.
  */
  public String getQueryString() {
    return m_queryString;
  }

 /**
  * Get the fragment for this URI.
  *
  * @return the fragment for this URI. Null is returned if there
  *         was no "#" in the URI spec, empty string if there was a
  *         "#" but no fragment following it.
  */
  public String getFragment() {
    return m_fragment;
  }

 /**
  * Set the scheme for this URI. The scheme is converted to lowercase
  * before it is set.
  *
  * @param p_scheme the scheme for this URI (cannot be null)
  *
  * @exception MalformedURIException if p_scheme is not a conformant
  *                                  scheme name
  */
  public void setScheme(String p_scheme) throws MalformedURIException {
    if (p_scheme == null) {
      throw new MalformedURIException(
                "Cannot set scheme from null string!");
    }
    if (!isConformantSchemeName(p_scheme)) {
      throw new MalformedURIException("The scheme is not conformant.");
    }

    m_scheme = p_scheme.toLowerCase();
  }

 /**
  * Set the userinfo for this URI. If a non-null value is passed in and
  * the host value is null, then an exception is thrown.
  *
  * @param p_userinfo the userinfo for this URI
  *
  * @exception MalformedURIException if p_userinfo contains invalid
  *                                  characters
  */
  public void setUserinfo(String p_userinfo) throws MalformedURIException {
    if (p_userinfo == null) {
      m_userinfo = null;
      return;
    }
    else {
      if (m_host == null) {
        throw new MalformedURIException(
                     "Userinfo cannot be set when host is null!");
      }

      // userinfo can contain alphanumerics, mark characters, escaped
      // and ';',':','&','=','+','$',','
      int index = 0;
      int end = p_userinfo.length();
      char testChar = '\0';
      while (index < end) {
        testChar = p_userinfo.charAt(index);
        if (testChar == '%') {
          if (index+2 >= end ||
              !isHex(p_userinfo.charAt(index+1)) ||
              !isHex(p_userinfo.charAt(index+2))) {
            throw new MalformedURIException(
                  "Userinfo contains invalid escape sequence!");
          }
        }
        else if (!isUserinfoCharacter(testChar)) {
          throw new MalformedURIException(
                  "Userinfo contains invalid character:"+testChar);
        }
        index++;
      }
    }
    m_userinfo = p_userinfo;
  }

 /**
  * <p>Set the host for this URI. If null is passed in, the userinfo
  * field is also set to null and the port is set to -1.</p>
  * 
  * <p>Note: This method overwrites registry based authority if it
  * previously existed in this URI.</p>
  *
  * @param p_host the host for this URI
  *
  * @exception MalformedURIException if p_host is not a valid IP
  *                                  address or DNS hostname.
  */
  public void setHost(String p_host) throws MalformedURIException {
    if (p_host == null || p_host.length() == 0) {
      if (p_host != null) {
        m_regAuthority = null;
      }
      m_host = p_host;
      m_userinfo = null;
      m_port = -1;
      return;
    }
    else if (!isWellFormedAddress(p_host)) {
      throw new MalformedURIException("Host is not a well formed address!");
    }
    m_host = p_host;
    m_regAuthority = null;
  }

 /**
  * Set the port for this URI. -1 is used to indicate that the port is
  * not specified, otherwise valid port numbers are  between 0 and 65535.
  * If a valid port number is passed in and the host field is null,
  * an exception is thrown.
  *
  * @param p_port the port number for this URI
  *
  * @exception MalformedURIException if p_port is not -1 and not a
  *                                  valid port number
  */
  public void setPort(int p_port) throws MalformedURIException {
    if (p_port >= 0 && p_port <= 65535) {
      if (m_host == null) {
        throw new MalformedURIException(
                      "Port cannot be set when host is null!");
      }
    }
    else if (p_port != -1) {
      throw new MalformedURIException("Invalid port number!");
    }
    m_port = p_port;
  }
  
  /**
   * <p>Sets the registry based authority for this URI.</p>
   * 
   * <p>Note: This method overwrites server based authority
   * if it previously existed in this URI.</p>
   * 
   * @param authority the registry based authority for this URI
   * 
   * @exception MalformedURIException it authority is not a
   * well formed registry based authority
   */
  public void setRegBasedAuthority(String authority) 
    throws MalformedURIException {

    if (authority == null) {
      m_regAuthority = null;
      return;
    }
  // reg_name = 1*( unreserved | escaped | "$" | "," | 
  //            ";" | ":" | "@" | "&" | "=" | "+" )
    else if (authority.length() < 1 ||
      !isValidRegistryBasedAuthority(authority) ||
      authority.indexOf('/') != -1) {
      throw new MalformedURIException("Registry based authority is not well formed.");        
    }
    m_regAuthority = authority;
    m_host = null;
    m_userinfo = null;
    m_port = -1;
  }

 /**
  * Set the path for this URI. If the supplied path is null, then the
  * query string and fragment are set to null as well. If the supplied
  * path includes a query string and/or fragment, these fields will be
  * parsed and set as well. Note that, for URIs following the "generic
  * URI" syntax, the path specified should start with a slash.
  * For URIs that do not follow the generic URI syntax, this method
  * sets the scheme-specific part.
  *
  * @param p_path the path for this URI (may be null)
  *
  * @exception MalformedURIException if p_path contains invalid
  *                                  characters
  */
  public void setPath(String p_path) throws MalformedURIException {
    if (p_path == null) {
      m_path = null;
      m_queryString = null;
      m_fragment = null;
    }
    else {
      initializePath(p_path, 0);
    }
  }

 /**
  * Append to the end of the path of this URI. If the current path does
  * not end in a slash and the path to be appended does not begin with
  * a slash, a slash will be appended to the current path before the
  * new segment is added. Also, if the current path ends in a slash
  * and the new segment begins with a slash, the extra slash will be
  * removed before the new segment is appended.
  *
  * @param p_addToPath the new segment to be added to the current path
  *
  * @exception MalformedURIException if p_addToPath contains syntax
  *                                  errors
  */
  public void appendPath(String p_addToPath)
                         throws MalformedURIException {
    if (p_addToPath == null || p_addToPath.trim().length() == 0) {
      return;
    }

    if (!isURIString(p_addToPath)) {
      throw new MalformedURIException(
              "Path contains invalid character!");
    }

    if (m_path == null || m_path.trim().length() == 0) {
      if (p_addToPath.startsWith("/")) {
        m_path = p_addToPath;
      }
      else {
        m_path = "/" + p_addToPath;
      }
    }
    else if (m_path.endsWith("/")) {
      if (p_addToPath.startsWith("/")) {
        m_path = m_path.concat(p_addToPath.substring(1));
      }
      else {
        m_path = m_path.concat(p_addToPath);
      }
    }
    else {
      if (p_addToPath.startsWith("/")) {
        m_path = m_path.concat(p_addToPath);
      }
      else {
        m_path = m_path.concat("/" + p_addToPath);
      }
    }
  }

 /**
  * Set the query string for this URI. A non-null value is valid only
  * if this is an URI conforming to the generic URI syntax and
  * the path value is not null.
  *
  * @param p_queryString the query string for this URI
  *
  * @exception MalformedURIException if p_queryString is not null and this
  *                                  URI does not conform to the generic
  *                                  URI syntax or if the path is null
  */
  public void setQueryString(String p_queryString) throws MalformedURIException {
    if (p_queryString == null) {
      m_queryString = null;
    }
    else if (!isGenericURI()) {
      throw new MalformedURIException(
              "Query string can only be set for a generic URI!");
    }
    else if (getPath() == null) {
      throw new MalformedURIException(
              "Query string cannot be set when path is null!");
    }
    else if (!isURIString(p_queryString)) {
      throw new MalformedURIException(
              "Query string contains invalid character!");
    }
    else {
      m_queryString = p_queryString;
    }
  }

 /**
  * Set the fragment for this URI. A non-null value is valid only
  * if this is a URI conforming to the generic URI syntax and
  * the path value is not null.
  *
  * @param p_fragment the fragment for this URI
  *
  * @exception MalformedURIException if p_fragment is not null and this
  *                                  URI does not conform to the generic
  *                                  URI syntax or if the path is null
  */
  public void setFragment(String p_fragment) throws MalformedURIException {
    if (p_fragment == null) {
      m_fragment = null;
    }
    else if (!isGenericURI()) {
      throw new MalformedURIException(
         "Fragment can only be set for a generic URI!");
    }
    else if (getPath() == null) {
      throw new MalformedURIException(
              "Fragment cannot be set when path is null!");
    }
    else if (!isURIString(p_fragment)) {
      throw new MalformedURIException(
              "Fragment contains invalid character!");
    }
    else {
      m_fragment = p_fragment;
    }
  }

 /**
  * Determines if the passed-in Object is equivalent to this URI.
  *
  * @param p_test the Object to test for equality.
  *
  * @return true if p_test is a URI with all values equal to this
  *         URI, false otherwise
  */
  @Override
  public boolean equals(Object p_test) {
    if (p_test instanceof URI) {
      URI testURI = (URI) p_test;
      if (((m_scheme == null && testURI.m_scheme == null) ||
           (m_scheme != null && testURI.m_scheme != null &&
            m_scheme.equals(testURI.m_scheme))) &&
          ((m_userinfo == null && testURI.m_userinfo == null) ||
           (m_userinfo != null && testURI.m_userinfo != null &&
            m_userinfo.equals(testURI.m_userinfo))) &&
          ((m_host == null && testURI.m_host == null) ||
           (m_host != null && testURI.m_host != null &&
            m_host.equals(testURI.m_host))) &&
            m_port == testURI.m_port &&
          ((m_path == null && testURI.m_path == null) ||
           (m_path != null && testURI.m_path != null &&
            m_path.equals(testURI.m_path))) &&
          ((m_queryString == null && testURI.m_queryString == null) ||
           (m_queryString != null && testURI.m_queryString != null &&
            m_queryString.equals(testURI.m_queryString))) &&
          ((m_fragment == null && testURI.m_fragment == null) ||
           (m_fragment != null && testURI.m_fragment != null &&
            m_fragment.equals(testURI.m_fragment)))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((m_fragment == null) ? 0 : m_fragment.hashCode());
    result = prime * result + ((m_host == null) ? 0 : m_host.hashCode());
    result = prime * result + ((m_path == null) ? 0 : m_path.hashCode());
    result = prime * result + m_port;
    result = prime * result + ((m_queryString == null) ? 0 : m_queryString.hashCode());
    result = prime * result + ((m_scheme == null) ? 0 : m_scheme.hashCode());
    result = prime * result + ((m_userinfo == null) ? 0 : m_userinfo.hashCode());
    return result;
  }

 /**
  * Get the URI as a string specification. See RFC 2396 Section 5.2.
  *
  * @return the URI string specification
  */
  @Override
  public String toString() {
    StringBuffer uriSpecString = new StringBuffer();

    if (m_scheme != null) {
      uriSpecString.append(m_scheme);
      uriSpecString.append(':');
    }
    uriSpecString.append(getSchemeSpecificPart());
    return uriSpecString.toString();
  }

 /**
  * Get the indicator as to whether this URI uses the "generic URI"
  * syntax.
  *
  * @return true if this URI uses the "generic URI" syntax, false
  *         otherwise
  */
  public boolean isGenericURI() {
    // presence of the host (whether valid or empty) means
    // double-slashes which means generic uri
    return (m_host != null);
  }

 /**
  * Determine whether a scheme conforms to the rules for a scheme name.
  * A scheme is conformant if it starts with an alphanumeric, and
  * contains only alphanumerics, '+','-' and '.'.
  *
  * @return true if the scheme is conformant, false otherwise
  */
  public static boolean isConformantSchemeName(String p_scheme) {
    if (p_scheme == null || p_scheme.trim().length() == 0) {
      return false;
    }

    if (!isAlpha(p_scheme.charAt(0))) {
      return false;
    }

    char testChar;
    int schemeLength = p_scheme.length();
    for (int i = 1; i < schemeLength; ++i) {
      testChar = p_scheme.charAt(i);
      if (!isSchemeCharacter(testChar)) {
        return false;
      }
    }

    return true;
  }

 /**
  * Determine whether a string is syntactically capable of representing
  * a valid IPv4 address, IPv6 reference or the domain name of a network host. 
  * A valid IPv4 address consists of four decimal digit groups separated by a
  * '.'. Each group must consist of one to three digits. See RFC 2732 Section 3,
  * and RFC 2373 Section 2.2, for the definition of IPv6 references. A hostname 
  * consists of domain labels (each of which must begin and end with an alphanumeric 
  * but may contain '-') separated & by a '.'. See RFC 2396 Section 3.2.2.
  *
  * @return true if the string is a syntactically valid IPv4 address, 
  * IPv6 reference or hostname
  */
  public static boolean isWellFormedAddress(String address) {
    if (address == null) {
      return false;
    }

    int addrLength = address.length();
    if (addrLength == 0) {
      return false;
    }
    
    // Check if the host is a valid IPv6reference.
    if (address.startsWith("[")) {
      return isWellFormedIPv6Reference(address);
    }

    // Cannot start with a '.', '-', or end with a '-'.
    if (address.startsWith(".") || 
        address.startsWith("-") || 
        address.endsWith("-")) {
      return false;
    }

    // rightmost domain label starting with digit indicates IP address
    // since top level domain label can only start with an alpha
    // see RFC 2396 Section 3.2.2
    int index = address.lastIndexOf('.');
    if (address.endsWith(".")) {
      index = address.substring(0, index).lastIndexOf('.');
    }

    if (index+1 < addrLength && isDigit(address.charAt(index+1))) {
      return isWellFormedIPv4Address(address);
    }
    else {
      // hostname      = *( domainlabel "." ) toplabel [ "." ]
      // domainlabel   = alphanum | alphanum *( alphanum | "-" ) alphanum
      // toplabel      = alpha | alpha *( alphanum | "-" ) alphanum
      
      // RFC 2396 states that hostnames take the form described in 
      // RFC 1034 (Section 3) and RFC 1123 (Section 2.1). According
      // to RFC 1034, hostnames are limited to 255 characters.
      if (addrLength > 255) {
        return false;
      }
      
      // domain labels can contain alphanumerics and '-"
      // but must start and end with an alphanumeric
      char testChar;
      int labelCharCount = 0;

      for (int i = 0; i < addrLength; i++) {
        testChar = address.charAt(i);
        if (testChar == '.') {
          if (!isAlphanum(address.charAt(i-1))) {
            return false;
          }
          if (i+1 < addrLength && !isAlphanum(address.charAt(i+1))) {
            return false;
          }
          labelCharCount = 0;
        }
        else if (!isAlphanum(testChar) && testChar != '-') {
          return false;
        }
        // RFC 1034: Labels must be 63 characters or less.
        else if (++labelCharCount > 63) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * <p>Determines whether a string is an IPv4 address as defined by 
   * RFC 2373, and under the further constraint that it must be a 32-bit
   * address. Though not expressed in the grammar, in order to satisfy 
   * the 32-bit address constraint, each segment of the address cannot 
   * be greater than 255 (8 bits of information).</p>
   *
   * <p><code>IPv4address = 1*3DIGIT "." 1*3DIGIT "." 1*3DIGIT "." 1*3DIGIT</code></p>
   *
   * @return true if the string is a syntactically valid IPv4 address
   */
  public static boolean isWellFormedIPv4Address(String address) {
      
      int addrLength = address.length();
      char testChar;
      int numDots = 0;
      int numDigits = 0;

      // make sure that 1) we see only digits and dot separators, 2) that
      // any dot separator is preceded and followed by a digit and
      // 3) that we find 3 dots
      //
      // RFC 2732 amended RFC 2396 by replacing the definition 
      // of IPv4address with the one defined by RFC 2373. - mrglavas
      //
      // IPv4address = 1*3DIGIT "." 1*3DIGIT "." 1*3DIGIT "." 1*3DIGIT
      //
      // One to three digits must be in each segment.
      for (int i = 0; i < addrLength; i++) {
        testChar = address.charAt(i);
        if (testChar == '.') {
          if ((i > 0 && !isDigit(address.charAt(i-1))) || 
              (i+1 < addrLength && !isDigit(address.charAt(i+1)))) {
            return false;
          }
          numDigits = 0;
          if (++numDots > 3) {
            return false;
          }
        }
        else if (!isDigit(testChar)) {
          return false;
        }
        // Check that that there are no more than three digits
        // in this segment.
        else if (++numDigits > 3) {
          return false;
        }
        // Check that this segment is not greater than 255.
        else if (numDigits == 3) {
          char first = address.charAt(i-2);
          char second = address.charAt(i-1);
          if (!(first < '2' || 
               (first == '2' && 
               (second < '5' || 
               (second == '5' && testChar <= '5'))))) {
            return false;
          }
        }
      }
      return (numDots == 3);
  }
  
  /**
   * <p>Determines whether a string is an IPv6 reference as defined
   * by RFC 2732, where IPv6address is defined in RFC 2373. The 
   * IPv6 address is parsed according to Section 2.2 of RFC 2373,
   * with the additional constraint that the address be composed of
   * 128 bits of information.</p>
   *
   * <p><code>IPv6reference = "[" IPv6address "]"</code></p>
   *
   * <p>Note: The BNF expressed in RFC 2373 Appendix B does not 
   * accurately describe section 2.2, and was in fact removed from
   * RFC 3513, the successor of RFC 2373.</p>
   *
   * @return true if the string is a syntactically valid IPv6 reference
   */
  public static boolean isWellFormedIPv6Reference(String address) {

      int addrLength = address.length();
      int index = 1;
      int end = addrLength-1;
      
      // Check if string is a potential match for IPv6reference.
      if (!(addrLength > 2 && address.charAt(0) == '[' 
          && address.charAt(end) == ']')) {
          return false;
      }
      
      // Counter for the number of 16-bit sections read in the address.
      int [] counter = new int[1];
      
      // Scan hex sequence before possible '::' or IPv4 address.
      index = scanHexSequence(address, index, end, counter);
      if (index == -1) {
          return false;
      }
      // Address must contain 128-bits of information.
      else if (index == end) {
          return (counter[0] == 8);
      }
      
      if (index+1 < end && address.charAt(index) == ':') {
          if (address.charAt(index+1) == ':') {
              // '::' represents at least one 16-bit group of zeros.
              if (++counter[0] > 8) {
                  return false;
              }
              index += 2;
              // Trailing zeros will fill out the rest of the address.
              if (index == end) {
                 return true;
              }
          }
          // If the second character wasn't ':', in order to be valid,
          // the remainder of the string must match IPv4Address, 
          // and we must have read exactly 6 16-bit groups.
          else {
              return (counter[0] == 6) && 
                  isWellFormedIPv4Address(address.substring(index+1, end));
          }
      }
      else {
          return false;
      }
      
      // 3. Scan hex sequence after '::'.
      int prevCount = counter[0];
      index = scanHexSequence(address, index, end, counter);

      // We've either reached the end of the string, the address ends in
      // an IPv4 address, or it is invalid. scanHexSequence has already 
      // made sure that we have the right number of bits. 
      return (index == end) || 
          (index != -1 && isWellFormedIPv4Address(
          address.substring((counter[0] > prevCount) ? index+1 : index, end)));
  }
  
  /**
   * Helper method for isWellFormedIPv6Reference which scans the 
   * hex sequences of an IPv6 address. It returns the index of the 
   * next character to scan in the address, or -1 if the string 
   * cannot match a valid IPv6 address. 
   *
   * @param address the string to be scanned
   * @param index the beginning index (inclusive)
   * @param end the ending index (exclusive)
   * @param counter a counter for the number of 16-bit sections read
   * in the address
   *
   * @return the index of the next character to scan, or -1 if the
   * string cannot match a valid IPv6 address
   */
  private static int scanHexSequence (String address, int index, int end, int [] counter) {
    
      char testChar;
      int numDigits = 0;
      int start = index;
      
      // Trying to match the following productions:
      // hexseq = hex4 *( ":" hex4)
      // hex4   = 1*4HEXDIG
      for (; index < end; ++index) {
        testChar = address.charAt(index);
        if (testChar == ':') {
            // IPv6 addresses are 128-bit, so there can be at most eight sections.
            if (numDigits > 0 && ++counter[0] > 8) {
                return -1;
            }
            // This could be '::'.
            if (numDigits == 0 || ((index+1 < end) && address.charAt(index+1) == ':')) {
                return index;
            }
            numDigits = 0;
        }
        // This might be invalid or an IPv4address. If it's potentially an IPv4address,
        // backup to just after the last valid character that matches hexseq.
        else if (!isHex(testChar)) {
            if (testChar == '.' && numDigits < 4 && numDigits > 0 && counter[0] <= 6) {
                int back = index - numDigits - 1;
                return (back >= start) ? back : (back+1);
            }
            return -1;
        }
        // There can be at most 4 hex digits per group.
        else if (++numDigits > 4) {
            return -1;
        }
      }
      return (numDigits > 0 && ++counter[0] <= 8) ? end : -1;
  } 


 /**
  * Determine whether a char is a digit.
  *
  * @return true if the char is betweeen '0' and '9', false otherwise
  */
  private static boolean isDigit(char p_char) {
    return p_char >= '0' && p_char <= '9';
  }

 /**
  * Determine whether a character is a hexadecimal character.
  *
  * @return true if the char is betweeen '0' and '9', 'a' and 'f'
  *         or 'A' and 'F', false otherwise
  */
  private static boolean isHex(char p_char) {
    return (p_char <= 'f' && (fgLookupTable[p_char] & ASCII_HEX_CHARACTERS) != 0);
  }

 /**
  * Determine whether a char is an alphabetic character: a-z or A-Z
  *
  * @return true if the char is alphabetic, false otherwise
  */
  private static boolean isAlpha(char p_char) {
      return ((p_char >= 'a' && p_char <= 'z') || (p_char >= 'A' && p_char <= 'Z' ));
  }

 /**
  * Determine whether a char is an alphanumeric: 0-9, a-z or A-Z
  *
  * @return true if the char is alphanumeric, false otherwise
  */
  private static boolean isAlphanum(char p_char) {
     return (p_char <= 'z' && (fgLookupTable[p_char] & MASK_ALPHA_NUMERIC) != 0);
  }

 /**
  * Determine whether a char is a URI character (reserved or 
  * unreserved, not including '%' for escaped octets).
  *
  * @return true if the char is a URI character, false otherwise
  */
  private static boolean isURICharacter (char p_char) {
      return (p_char <= '~' && (fgLookupTable[p_char] & MASK_URI_CHARACTER) != 0);
  }

 /**
  * Determine whether a char is a scheme character.
  *
  * @return true if the char is a scheme character, false otherwise
  */
  private static boolean isSchemeCharacter (char p_char) {
      return (p_char <= 'z' && (fgLookupTable[p_char] & MASK_SCHEME_CHARACTER) != 0);
  }

 /**
  * Determine whether a char is a userinfo character.
  *
  * @return true if the char is a userinfo character, false otherwise
  */
  private static boolean isUserinfoCharacter (char p_char) {
      return (p_char <= 'z' && (fgLookupTable[p_char] & MASK_USERINFO_CHARACTER) != 0);
  }
  
 /**
  * Determine whether a char is a path character.
  * 
  * @return true if the char is a path character, false otherwise
  */
  private static boolean isPathCharacter (char p_char) {
      return (p_char <= '~' && (fgLookupTable[p_char] & MASK_PATH_CHARACTER) != 0);
  }


 /**
  * Determine whether a given string contains only URI characters (also
  * called "uric" in RFC 2396). uric consist of all reserved
  * characters, unreserved characters and escaped characters.
  *
  * @return true if the string is comprised of uric, false otherwise
  */
  private static boolean isURIString(String p_uric) {
    if (p_uric == null) {
      return false;
    }
    int end = p_uric.length();
    char testChar = '\0';
    for (int i = 0; i < end; i++) {
      testChar = p_uric.charAt(i);
      if (testChar == '%') {
        if (i+2 >= end ||
            !isHex(p_uric.charAt(i+1)) ||
            !isHex(p_uric.charAt(i+2))) {
          return false;
        }
        else {
          i += 2;
          continue;
        }
      }
      if (isURICharacter(testChar)) {
          continue;
      }
      else {
        return false;
      }
    }
    return true;
  }
  //
  // XML Schema anyURI specific information
  //
  
  // which ASCII characters need to be escaped
  private static boolean gNeedEscaping[] = new boolean[128];
  // the first hex character if a character needs to be escaped
  private static char gAfterEscaping1[] = new char[128];
  // the second hex character if a character needs to be escaped
  private static char gAfterEscaping2[] = new char[128];
  private static char[] gHexChs = {'0', '1', '2', '3', '4', '5', '6', '7',
                                   '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
  // initialize the above 3 arrays
  static {
      for (int i = 0; i <= 0x1f; i++) {
          gNeedEscaping[i] = true;
          gAfterEscaping1[i] = gHexChs[i >> 4];
          gAfterEscaping2[i] = gHexChs[i & 0xf];
      }
      gNeedEscaping[0x7f] = true;
      gAfterEscaping1[0x7f] = '7';
      gAfterEscaping2[0x7f] = 'F';
      char[] escChs = {' ', '<', '>', '"', '{', '}',
                       '|', '\\', '^', '~', '`'};
      int len = escChs.length;
      char ch;
      for (int i = 0; i < len; i++) {
          ch = escChs[i];
          gNeedEscaping[ch] = true;
          gAfterEscaping1[ch] = gHexChs[ch >> 4];
          gAfterEscaping2[ch] = gHexChs[ch & 0xf];
      }
  }

  // To encode special characters in anyURI, by using %HH to represent
  // special ASCII characters: 0x00~0x1F, 0x7F, ' ', '<', '>', etc.
  // and non-ASCII characters (whose value >= 128).
  public static String encode(String anyURI){
      int len = anyURI.length(), ch;
      StringBuffer buffer = new StringBuffer(len*3);

      // for each character in the anyURI
      int i = 0;
      for (; i < len; i++) {
          ch = anyURI.charAt(i);
          // if it's not an ASCII character, break here, and use UTF-8 encoding
          if (ch >= 128)
              break;
          if (gNeedEscaping[ch]) {
              buffer.append('%');
              buffer.append(gAfterEscaping1[ch]);
              buffer.append(gAfterEscaping2[ch]);
          }
          else {
              buffer.append((char)ch);
          }
      }

      // we saw some non-ascii character
      if (i < len) {
          // get UTF-8 bytes for the remaining sub-string
          byte[] bytes = null;
          byte b;
          bytes = new byte[0];
          // TODO
          /*
          try {
              bytes = anyURI.substring(i).getBytes("UTF-8");
          } catch (java.io.UnsupportedEncodingException e) {
              // should never happen
              return anyURI;
          }
          */
          len = bytes.length;

          // for each byte
          for (i = 0; i < len; i++) {
              b = bytes[i];
              // for non-ascii character: make it positive, then escape
              if (b < 0) {
                  ch = b + 256;
                  buffer.append('%');
                  buffer.append(gHexChs[ch >> 4]);
                  buffer.append(gHexChs[ch & 0xf]);
              }
              else if (gNeedEscaping[b]) {
                  buffer.append('%');
                  buffer.append(gAfterEscaping1[b]);
                  buffer.append(gAfterEscaping2[b]);
              }
              else {
                  buffer.append((char)b);
              }
          }
      }

      // If encoding happened, create a new string;
      // otherwise, return the orginal one.
      if (buffer.length() != len)
          return buffer.toString();
      else
          return anyURI;
  }

}

 /**
  * This class defines the basic XML character properties. The data
  * in this class can be used to verify that a character is a valid
  * XML character or if the character is a space, name start, or name
  * character.
  * <p>
  * A series of convenience methods are supplied to ease the burden
  * of the developer. Because inlining the checks can improve per
  * character performance, the tables of character properties are
  * public. Using the character as an index into the <code>CHARS</code>
  * array and applying the appropriate mask flag (e.g.
  * <code>MASK_VALID</code>), yields the same results as calling the
  * convenience methods. There is one exception: check the comments
  * for the <code>isValid</code> method for details.
  *
  * @author Glenn Marcy, IBM
  * @author Andy Clark, IBM
  * @author Eric Ye, IBM
  * @author Arnaud  Le Hors, IBM
  * @author Michael Glavassevich, IBM
  * @author Rahul Srivastava, Sun Microsystems Inc.
  */
 public static final class XMLChar {

     //
     // Constants
     //

     /** Character flags. */
     private static final byte[] CHARS = new byte[1 << 16];

     /** Valid character mask. */
     public static final int MASK_VALID = 0x01;

     /** Space character mask. */
     public static final int MASK_SPACE = 0x02;

     /** Name start character mask. */
     public static final int MASK_NAME_START = 0x04;

     /** Name character mask. */
     public static final int MASK_NAME = 0x08;

     /** Pubid character mask. */
     public static final int MASK_PUBID = 0x10;
     
     /** 
      * Content character mask. Special characters are those that can
      * be considered the start of markup, such as '&lt;' and '&amp;'. 
      * The various newline characters are considered special as well.
      * All other valid XML characters can be considered content.
      * <p>
      * This is an optimization for the inner loop of character scanning.
      */
     public static final int MASK_CONTENT = 0x20;

     /** NCName start character mask. */
     public static final int MASK_NCNAME_START = 0x40;

     /** NCName character mask. */
     public static final int MASK_NCNAME = 0x80;

     //
     // Static initialization
     //

     static {
         
         // Initializing the Character Flag Array
         // Code generated by: XMLCharGenerator.
         
         CHARS[9] = 35;
         CHARS[10] = 19;
         CHARS[13] = 19;
         CHARS[32] = 51;
         CHARS[33] = 49;
         CHARS[34] = 33;
         Arrays.fill(CHARS, 35, 38, (byte) 49 ); // Fill 3 of value (byte) 49
         CHARS[38] = 1;
         Arrays.fill(CHARS, 39, 45, (byte) 49 ); // Fill 6 of value (byte) 49
         Arrays.fill(CHARS, 45, 47, (byte) -71 ); // Fill 2 of value (byte) -71
         CHARS[47] = 49;
         Arrays.fill(CHARS, 48, 58, (byte) -71 ); // Fill 10 of value (byte) -71
         CHARS[58] = 61;
         CHARS[59] = 49;
         CHARS[60] = 1;
         CHARS[61] = 49;
         CHARS[62] = 33;
         Arrays.fill(CHARS, 63, 65, (byte) 49 ); // Fill 2 of value (byte) 49
         Arrays.fill(CHARS, 65, 91, (byte) -3 ); // Fill 26 of value (byte) -3
         Arrays.fill(CHARS, 91, 93, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[93] = 1;
         CHARS[94] = 33;
         CHARS[95] = -3;
         CHARS[96] = 33;
         Arrays.fill(CHARS, 97, 123, (byte) -3 ); // Fill 26 of value (byte) -3
         Arrays.fill(CHARS, 123, 183, (byte) 33 ); // Fill 60 of value (byte) 33
         CHARS[183] = -87;
         Arrays.fill(CHARS, 184, 192, (byte) 33 ); // Fill 8 of value (byte) 33
         Arrays.fill(CHARS, 192, 215, (byte) -19 ); // Fill 23 of value (byte) -19
         CHARS[215] = 33;
         Arrays.fill(CHARS, 216, 247, (byte) -19 ); // Fill 31 of value (byte) -19
         CHARS[247] = 33;
         Arrays.fill(CHARS, 248, 306, (byte) -19 ); // Fill 58 of value (byte) -19
         Arrays.fill(CHARS, 306, 308, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 308, 319, (byte) -19 ); // Fill 11 of value (byte) -19
         Arrays.fill(CHARS, 319, 321, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 321, 329, (byte) -19 ); // Fill 8 of value (byte) -19
         CHARS[329] = 33;
         Arrays.fill(CHARS, 330, 383, (byte) -19 ); // Fill 53 of value (byte) -19
         CHARS[383] = 33;
         Arrays.fill(CHARS, 384, 452, (byte) -19 ); // Fill 68 of value (byte) -19
         Arrays.fill(CHARS, 452, 461, (byte) 33 ); // Fill 9 of value (byte) 33
         Arrays.fill(CHARS, 461, 497, (byte) -19 ); // Fill 36 of value (byte) -19
         Arrays.fill(CHARS, 497, 500, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 500, 502, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 502, 506, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 506, 536, (byte) -19 ); // Fill 30 of value (byte) -19
         Arrays.fill(CHARS, 536, 592, (byte) 33 ); // Fill 56 of value (byte) 33
         Arrays.fill(CHARS, 592, 681, (byte) -19 ); // Fill 89 of value (byte) -19
         Arrays.fill(CHARS, 681, 699, (byte) 33 ); // Fill 18 of value (byte) 33
         Arrays.fill(CHARS, 699, 706, (byte) -19 ); // Fill 7 of value (byte) -19
         Arrays.fill(CHARS, 706, 720, (byte) 33 ); // Fill 14 of value (byte) 33
         Arrays.fill(CHARS, 720, 722, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 722, 768, (byte) 33 ); // Fill 46 of value (byte) 33
         Arrays.fill(CHARS, 768, 838, (byte) -87 ); // Fill 70 of value (byte) -87
         Arrays.fill(CHARS, 838, 864, (byte) 33 ); // Fill 26 of value (byte) 33
         Arrays.fill(CHARS, 864, 866, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 866, 902, (byte) 33 ); // Fill 36 of value (byte) 33
         CHARS[902] = -19;
         CHARS[903] = -87;
         Arrays.fill(CHARS, 904, 907, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[907] = 33;
         CHARS[908] = -19;
         CHARS[909] = 33;
         Arrays.fill(CHARS, 910, 930, (byte) -19 ); // Fill 20 of value (byte) -19
         CHARS[930] = 33;
         Arrays.fill(CHARS, 931, 975, (byte) -19 ); // Fill 44 of value (byte) -19
         CHARS[975] = 33;
         Arrays.fill(CHARS, 976, 983, (byte) -19 ); // Fill 7 of value (byte) -19
         Arrays.fill(CHARS, 983, 986, (byte) 33 ); // Fill 3 of value (byte) 33
         CHARS[986] = -19;
         CHARS[987] = 33;
         CHARS[988] = -19;
         CHARS[989] = 33;
         CHARS[990] = -19;
         CHARS[991] = 33;
         CHARS[992] = -19;
         CHARS[993] = 33;
         Arrays.fill(CHARS, 994, 1012, (byte) -19 ); // Fill 18 of value (byte) -19
         Arrays.fill(CHARS, 1012, 1025, (byte) 33 ); // Fill 13 of value (byte) 33
         Arrays.fill(CHARS, 1025, 1037, (byte) -19 ); // Fill 12 of value (byte) -19
         CHARS[1037] = 33;
         Arrays.fill(CHARS, 1038, 1104, (byte) -19 ); // Fill 66 of value (byte) -19
         CHARS[1104] = 33;
         Arrays.fill(CHARS, 1105, 1117, (byte) -19 ); // Fill 12 of value (byte) -19
         CHARS[1117] = 33;
         Arrays.fill(CHARS, 1118, 1154, (byte) -19 ); // Fill 36 of value (byte) -19
         CHARS[1154] = 33;
         Arrays.fill(CHARS, 1155, 1159, (byte) -87 ); // Fill 4 of value (byte) -87
         Arrays.fill(CHARS, 1159, 1168, (byte) 33 ); // Fill 9 of value (byte) 33
         Arrays.fill(CHARS, 1168, 1221, (byte) -19 ); // Fill 53 of value (byte) -19
         Arrays.fill(CHARS, 1221, 1223, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 1223, 1225, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 1225, 1227, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 1227, 1229, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 1229, 1232, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 1232, 1260, (byte) -19 ); // Fill 28 of value (byte) -19
         Arrays.fill(CHARS, 1260, 1262, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 1262, 1270, (byte) -19 ); // Fill 8 of value (byte) -19
         Arrays.fill(CHARS, 1270, 1272, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 1272, 1274, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 1274, 1329, (byte) 33 ); // Fill 55 of value (byte) 33
         Arrays.fill(CHARS, 1329, 1367, (byte) -19 ); // Fill 38 of value (byte) -19
         Arrays.fill(CHARS, 1367, 1369, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[1369] = -19;
         Arrays.fill(CHARS, 1370, 1377, (byte) 33 ); // Fill 7 of value (byte) 33
         Arrays.fill(CHARS, 1377, 1415, (byte) -19 ); // Fill 38 of value (byte) -19
         Arrays.fill(CHARS, 1415, 1425, (byte) 33 ); // Fill 10 of value (byte) 33
         Arrays.fill(CHARS, 1425, 1442, (byte) -87 ); // Fill 17 of value (byte) -87
         CHARS[1442] = 33;
         Arrays.fill(CHARS, 1443, 1466, (byte) -87 ); // Fill 23 of value (byte) -87
         CHARS[1466] = 33;
         Arrays.fill(CHARS, 1467, 1470, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[1470] = 33;
         CHARS[1471] = -87;
         CHARS[1472] = 33;
         Arrays.fill(CHARS, 1473, 1475, (byte) -87 ); // Fill 2 of value (byte) -87
         CHARS[1475] = 33;
         CHARS[1476] = -87;
         Arrays.fill(CHARS, 1477, 1488, (byte) 33 ); // Fill 11 of value (byte) 33
         Arrays.fill(CHARS, 1488, 1515, (byte) -19 ); // Fill 27 of value (byte) -19
         Arrays.fill(CHARS, 1515, 1520, (byte) 33 ); // Fill 5 of value (byte) 33
         Arrays.fill(CHARS, 1520, 1523, (byte) -19 ); // Fill 3 of value (byte) -19
         Arrays.fill(CHARS, 1523, 1569, (byte) 33 ); // Fill 46 of value (byte) 33
         Arrays.fill(CHARS, 1569, 1595, (byte) -19 ); // Fill 26 of value (byte) -19
         Arrays.fill(CHARS, 1595, 1600, (byte) 33 ); // Fill 5 of value (byte) 33
         CHARS[1600] = -87;
         Arrays.fill(CHARS, 1601, 1611, (byte) -19 ); // Fill 10 of value (byte) -19
         Arrays.fill(CHARS, 1611, 1619, (byte) -87 ); // Fill 8 of value (byte) -87
         Arrays.fill(CHARS, 1619, 1632, (byte) 33 ); // Fill 13 of value (byte) 33
         Arrays.fill(CHARS, 1632, 1642, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 1642, 1648, (byte) 33 ); // Fill 6 of value (byte) 33
         CHARS[1648] = -87;
         Arrays.fill(CHARS, 1649, 1720, (byte) -19 ); // Fill 71 of value (byte) -19
         Arrays.fill(CHARS, 1720, 1722, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 1722, 1727, (byte) -19 ); // Fill 5 of value (byte) -19
         CHARS[1727] = 33;
         Arrays.fill(CHARS, 1728, 1743, (byte) -19 ); // Fill 15 of value (byte) -19
         CHARS[1743] = 33;
         Arrays.fill(CHARS, 1744, 1748, (byte) -19 ); // Fill 4 of value (byte) -19
         CHARS[1748] = 33;
         CHARS[1749] = -19;
         Arrays.fill(CHARS, 1750, 1765, (byte) -87 ); // Fill 15 of value (byte) -87
         Arrays.fill(CHARS, 1765, 1767, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 1767, 1769, (byte) -87 ); // Fill 2 of value (byte) -87
         CHARS[1769] = 33;
         Arrays.fill(CHARS, 1770, 1774, (byte) -87 ); // Fill 4 of value (byte) -87
         Arrays.fill(CHARS, 1774, 1776, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 1776, 1786, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 1786, 2305, (byte) 33 ); // Fill 519 of value (byte) 33
         Arrays.fill(CHARS, 2305, 2308, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[2308] = 33;
         Arrays.fill(CHARS, 2309, 2362, (byte) -19 ); // Fill 53 of value (byte) -19
         Arrays.fill(CHARS, 2362, 2364, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[2364] = -87;
         CHARS[2365] = -19;
         Arrays.fill(CHARS, 2366, 2382, (byte) -87 ); // Fill 16 of value (byte) -87
         Arrays.fill(CHARS, 2382, 2385, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2385, 2389, (byte) -87 ); // Fill 4 of value (byte) -87
         Arrays.fill(CHARS, 2389, 2392, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2392, 2402, (byte) -19 ); // Fill 10 of value (byte) -19
         Arrays.fill(CHARS, 2402, 2404, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 2404, 2406, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2406, 2416, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 2416, 2433, (byte) 33 ); // Fill 17 of value (byte) 33
         Arrays.fill(CHARS, 2433, 2436, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[2436] = 33;
         Arrays.fill(CHARS, 2437, 2445, (byte) -19 ); // Fill 8 of value (byte) -19
         Arrays.fill(CHARS, 2445, 2447, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2447, 2449, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 2449, 2451, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2451, 2473, (byte) -19 ); // Fill 22 of value (byte) -19
         CHARS[2473] = 33;
         Arrays.fill(CHARS, 2474, 2481, (byte) -19 ); // Fill 7 of value (byte) -19
         CHARS[2481] = 33;
         CHARS[2482] = -19;
         Arrays.fill(CHARS, 2483, 2486, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2486, 2490, (byte) -19 ); // Fill 4 of value (byte) -19
         Arrays.fill(CHARS, 2490, 2492, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[2492] = -87;
         CHARS[2493] = 33;
         Arrays.fill(CHARS, 2494, 2501, (byte) -87 ); // Fill 7 of value (byte) -87
         Arrays.fill(CHARS, 2501, 2503, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2503, 2505, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 2505, 2507, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2507, 2510, (byte) -87 ); // Fill 3 of value (byte) -87
         Arrays.fill(CHARS, 2510, 2519, (byte) 33 ); // Fill 9 of value (byte) 33
         CHARS[2519] = -87;
         Arrays.fill(CHARS, 2520, 2524, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 2524, 2526, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[2526] = 33;
         Arrays.fill(CHARS, 2527, 2530, (byte) -19 ); // Fill 3 of value (byte) -19
         Arrays.fill(CHARS, 2530, 2532, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 2532, 2534, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2534, 2544, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 2544, 2546, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 2546, 2562, (byte) 33 ); // Fill 16 of value (byte) 33
         CHARS[2562] = -87;
         Arrays.fill(CHARS, 2563, 2565, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2565, 2571, (byte) -19 ); // Fill 6 of value (byte) -19
         Arrays.fill(CHARS, 2571, 2575, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 2575, 2577, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 2577, 2579, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2579, 2601, (byte) -19 ); // Fill 22 of value (byte) -19
         CHARS[2601] = 33;
         Arrays.fill(CHARS, 2602, 2609, (byte) -19 ); // Fill 7 of value (byte) -19
         CHARS[2609] = 33;
         Arrays.fill(CHARS, 2610, 2612, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[2612] = 33;
         Arrays.fill(CHARS, 2613, 2615, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[2615] = 33;
         Arrays.fill(CHARS, 2616, 2618, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 2618, 2620, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[2620] = -87;
         CHARS[2621] = 33;
         Arrays.fill(CHARS, 2622, 2627, (byte) -87 ); // Fill 5 of value (byte) -87
         Arrays.fill(CHARS, 2627, 2631, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 2631, 2633, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 2633, 2635, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2635, 2638, (byte) -87 ); // Fill 3 of value (byte) -87
         Arrays.fill(CHARS, 2638, 2649, (byte) 33 ); // Fill 11 of value (byte) 33
         Arrays.fill(CHARS, 2649, 2653, (byte) -19 ); // Fill 4 of value (byte) -19
         CHARS[2653] = 33;
         CHARS[2654] = -19;
         Arrays.fill(CHARS, 2655, 2662, (byte) 33 ); // Fill 7 of value (byte) 33
         Arrays.fill(CHARS, 2662, 2674, (byte) -87 ); // Fill 12 of value (byte) -87
         Arrays.fill(CHARS, 2674, 2677, (byte) -19 ); // Fill 3 of value (byte) -19
         Arrays.fill(CHARS, 2677, 2689, (byte) 33 ); // Fill 12 of value (byte) 33
         Arrays.fill(CHARS, 2689, 2692, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[2692] = 33;
         Arrays.fill(CHARS, 2693, 2700, (byte) -19 ); // Fill 7 of value (byte) -19
         CHARS[2700] = 33;
         CHARS[2701] = -19;
         CHARS[2702] = 33;
         Arrays.fill(CHARS, 2703, 2706, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[2706] = 33;
         Arrays.fill(CHARS, 2707, 2729, (byte) -19 ); // Fill 22 of value (byte) -19
         CHARS[2729] = 33;
         Arrays.fill(CHARS, 2730, 2737, (byte) -19 ); // Fill 7 of value (byte) -19
         CHARS[2737] = 33;
         Arrays.fill(CHARS, 2738, 2740, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[2740] = 33;
         Arrays.fill(CHARS, 2741, 2746, (byte) -19 ); // Fill 5 of value (byte) -19
         Arrays.fill(CHARS, 2746, 2748, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[2748] = -87;
         CHARS[2749] = -19;
         Arrays.fill(CHARS, 2750, 2758, (byte) -87 ); // Fill 8 of value (byte) -87
         CHARS[2758] = 33;
         Arrays.fill(CHARS, 2759, 2762, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[2762] = 33;
         Arrays.fill(CHARS, 2763, 2766, (byte) -87 ); // Fill 3 of value (byte) -87
         Arrays.fill(CHARS, 2766, 2784, (byte) 33 ); // Fill 18 of value (byte) 33
         CHARS[2784] = -19;
         Arrays.fill(CHARS, 2785, 2790, (byte) 33 ); // Fill 5 of value (byte) 33
         Arrays.fill(CHARS, 2790, 2800, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 2800, 2817, (byte) 33 ); // Fill 17 of value (byte) 33
         Arrays.fill(CHARS, 2817, 2820, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[2820] = 33;
         Arrays.fill(CHARS, 2821, 2829, (byte) -19 ); // Fill 8 of value (byte) -19
         Arrays.fill(CHARS, 2829, 2831, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2831, 2833, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 2833, 2835, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2835, 2857, (byte) -19 ); // Fill 22 of value (byte) -19
         CHARS[2857] = 33;
         Arrays.fill(CHARS, 2858, 2865, (byte) -19 ); // Fill 7 of value (byte) -19
         CHARS[2865] = 33;
         Arrays.fill(CHARS, 2866, 2868, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 2868, 2870, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2870, 2874, (byte) -19 ); // Fill 4 of value (byte) -19
         Arrays.fill(CHARS, 2874, 2876, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[2876] = -87;
         CHARS[2877] = -19;
         Arrays.fill(CHARS, 2878, 2884, (byte) -87 ); // Fill 6 of value (byte) -87
         Arrays.fill(CHARS, 2884, 2887, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2887, 2889, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 2889, 2891, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 2891, 2894, (byte) -87 ); // Fill 3 of value (byte) -87
         Arrays.fill(CHARS, 2894, 2902, (byte) 33 ); // Fill 8 of value (byte) 33
         Arrays.fill(CHARS, 2902, 2904, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 2904, 2908, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 2908, 2910, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[2910] = 33;
         Arrays.fill(CHARS, 2911, 2914, (byte) -19 ); // Fill 3 of value (byte) -19
         Arrays.fill(CHARS, 2914, 2918, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 2918, 2928, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 2928, 2946, (byte) 33 ); // Fill 18 of value (byte) 33
         Arrays.fill(CHARS, 2946, 2948, (byte) -87 ); // Fill 2 of value (byte) -87
         CHARS[2948] = 33;
         Arrays.fill(CHARS, 2949, 2955, (byte) -19 ); // Fill 6 of value (byte) -19
         Arrays.fill(CHARS, 2955, 2958, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2958, 2961, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[2961] = 33;
         Arrays.fill(CHARS, 2962, 2966, (byte) -19 ); // Fill 4 of value (byte) -19
         Arrays.fill(CHARS, 2966, 2969, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2969, 2971, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[2971] = 33;
         CHARS[2972] = -19;
         CHARS[2973] = 33;
         Arrays.fill(CHARS, 2974, 2976, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 2976, 2979, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2979, 2981, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 2981, 2984, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2984, 2987, (byte) -19 ); // Fill 3 of value (byte) -19
         Arrays.fill(CHARS, 2987, 2990, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 2990, 2998, (byte) -19 ); // Fill 8 of value (byte) -19
         CHARS[2998] = 33;
         Arrays.fill(CHARS, 2999, 3002, (byte) -19 ); // Fill 3 of value (byte) -19
         Arrays.fill(CHARS, 3002, 3006, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3006, 3011, (byte) -87 ); // Fill 5 of value (byte) -87
         Arrays.fill(CHARS, 3011, 3014, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 3014, 3017, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[3017] = 33;
         Arrays.fill(CHARS, 3018, 3022, (byte) -87 ); // Fill 4 of value (byte) -87
         Arrays.fill(CHARS, 3022, 3031, (byte) 33 ); // Fill 9 of value (byte) 33
         CHARS[3031] = -87;
         Arrays.fill(CHARS, 3032, 3047, (byte) 33 ); // Fill 15 of value (byte) 33
         Arrays.fill(CHARS, 3047, 3056, (byte) -87 ); // Fill 9 of value (byte) -87
         Arrays.fill(CHARS, 3056, 3073, (byte) 33 ); // Fill 17 of value (byte) 33
         Arrays.fill(CHARS, 3073, 3076, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[3076] = 33;
         Arrays.fill(CHARS, 3077, 3085, (byte) -19 ); // Fill 8 of value (byte) -19
         CHARS[3085] = 33;
         Arrays.fill(CHARS, 3086, 3089, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[3089] = 33;
         Arrays.fill(CHARS, 3090, 3113, (byte) -19 ); // Fill 23 of value (byte) -19
         CHARS[3113] = 33;
         Arrays.fill(CHARS, 3114, 3124, (byte) -19 ); // Fill 10 of value (byte) -19
         CHARS[3124] = 33;
         Arrays.fill(CHARS, 3125, 3130, (byte) -19 ); // Fill 5 of value (byte) -19
         Arrays.fill(CHARS, 3130, 3134, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3134, 3141, (byte) -87 ); // Fill 7 of value (byte) -87
         CHARS[3141] = 33;
         Arrays.fill(CHARS, 3142, 3145, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[3145] = 33;
         Arrays.fill(CHARS, 3146, 3150, (byte) -87 ); // Fill 4 of value (byte) -87
         Arrays.fill(CHARS, 3150, 3157, (byte) 33 ); // Fill 7 of value (byte) 33
         Arrays.fill(CHARS, 3157, 3159, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 3159, 3168, (byte) 33 ); // Fill 9 of value (byte) 33
         Arrays.fill(CHARS, 3168, 3170, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 3170, 3174, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3174, 3184, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 3184, 3202, (byte) 33 ); // Fill 18 of value (byte) 33
         Arrays.fill(CHARS, 3202, 3204, (byte) -87 ); // Fill 2 of value (byte) -87
         CHARS[3204] = 33;
         Arrays.fill(CHARS, 3205, 3213, (byte) -19 ); // Fill 8 of value (byte) -19
         CHARS[3213] = 33;
         Arrays.fill(CHARS, 3214, 3217, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[3217] = 33;
         Arrays.fill(CHARS, 3218, 3241, (byte) -19 ); // Fill 23 of value (byte) -19
         CHARS[3241] = 33;
         Arrays.fill(CHARS, 3242, 3252, (byte) -19 ); // Fill 10 of value (byte) -19
         CHARS[3252] = 33;
         Arrays.fill(CHARS, 3253, 3258, (byte) -19 ); // Fill 5 of value (byte) -19
         Arrays.fill(CHARS, 3258, 3262, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3262, 3269, (byte) -87 ); // Fill 7 of value (byte) -87
         CHARS[3269] = 33;
         Arrays.fill(CHARS, 3270, 3273, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[3273] = 33;
         Arrays.fill(CHARS, 3274, 3278, (byte) -87 ); // Fill 4 of value (byte) -87
         Arrays.fill(CHARS, 3278, 3285, (byte) 33 ); // Fill 7 of value (byte) 33
         Arrays.fill(CHARS, 3285, 3287, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 3287, 3294, (byte) 33 ); // Fill 7 of value (byte) 33
         CHARS[3294] = -19;
         CHARS[3295] = 33;
         Arrays.fill(CHARS, 3296, 3298, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 3298, 3302, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3302, 3312, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 3312, 3330, (byte) 33 ); // Fill 18 of value (byte) 33
         Arrays.fill(CHARS, 3330, 3332, (byte) -87 ); // Fill 2 of value (byte) -87
         CHARS[3332] = 33;
         Arrays.fill(CHARS, 3333, 3341, (byte) -19 ); // Fill 8 of value (byte) -19
         CHARS[3341] = 33;
         Arrays.fill(CHARS, 3342, 3345, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[3345] = 33;
         Arrays.fill(CHARS, 3346, 3369, (byte) -19 ); // Fill 23 of value (byte) -19
         CHARS[3369] = 33;
         Arrays.fill(CHARS, 3370, 3386, (byte) -19 ); // Fill 16 of value (byte) -19
         Arrays.fill(CHARS, 3386, 3390, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3390, 3396, (byte) -87 ); // Fill 6 of value (byte) -87
         Arrays.fill(CHARS, 3396, 3398, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 3398, 3401, (byte) -87 ); // Fill 3 of value (byte) -87
         CHARS[3401] = 33;
         Arrays.fill(CHARS, 3402, 3406, (byte) -87 ); // Fill 4 of value (byte) -87
         Arrays.fill(CHARS, 3406, 3415, (byte) 33 ); // Fill 9 of value (byte) 33
         CHARS[3415] = -87;
         Arrays.fill(CHARS, 3416, 3424, (byte) 33 ); // Fill 8 of value (byte) 33
         Arrays.fill(CHARS, 3424, 3426, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 3426, 3430, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3430, 3440, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 3440, 3585, (byte) 33 ); // Fill 145 of value (byte) 33
         Arrays.fill(CHARS, 3585, 3631, (byte) -19 ); // Fill 46 of value (byte) -19
         CHARS[3631] = 33;
         CHARS[3632] = -19;
         CHARS[3633] = -87;
         Arrays.fill(CHARS, 3634, 3636, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 3636, 3643, (byte) -87 ); // Fill 7 of value (byte) -87
         Arrays.fill(CHARS, 3643, 3648, (byte) 33 ); // Fill 5 of value (byte) 33
         Arrays.fill(CHARS, 3648, 3654, (byte) -19 ); // Fill 6 of value (byte) -19
         Arrays.fill(CHARS, 3654, 3663, (byte) -87 ); // Fill 9 of value (byte) -87
         CHARS[3663] = 33;
         Arrays.fill(CHARS, 3664, 3674, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 3674, 3713, (byte) 33 ); // Fill 39 of value (byte) 33
         Arrays.fill(CHARS, 3713, 3715, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[3715] = 33;
         CHARS[3716] = -19;
         Arrays.fill(CHARS, 3717, 3719, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 3719, 3721, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[3721] = 33;
         CHARS[3722] = -19;
         Arrays.fill(CHARS, 3723, 3725, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[3725] = -19;
         Arrays.fill(CHARS, 3726, 3732, (byte) 33 ); // Fill 6 of value (byte) 33
         Arrays.fill(CHARS, 3732, 3736, (byte) -19 ); // Fill 4 of value (byte) -19
         CHARS[3736] = 33;
         Arrays.fill(CHARS, 3737, 3744, (byte) -19 ); // Fill 7 of value (byte) -19
         CHARS[3744] = 33;
         Arrays.fill(CHARS, 3745, 3748, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[3748] = 33;
         CHARS[3749] = -19;
         CHARS[3750] = 33;
         CHARS[3751] = -19;
         Arrays.fill(CHARS, 3752, 3754, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 3754, 3756, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[3756] = 33;
         Arrays.fill(CHARS, 3757, 3759, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[3759] = 33;
         CHARS[3760] = -19;
         CHARS[3761] = -87;
         Arrays.fill(CHARS, 3762, 3764, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 3764, 3770, (byte) -87 ); // Fill 6 of value (byte) -87
         CHARS[3770] = 33;
         Arrays.fill(CHARS, 3771, 3773, (byte) -87 ); // Fill 2 of value (byte) -87
         CHARS[3773] = -19;
         Arrays.fill(CHARS, 3774, 3776, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 3776, 3781, (byte) -19 ); // Fill 5 of value (byte) -19
         CHARS[3781] = 33;
         CHARS[3782] = -87;
         CHARS[3783] = 33;
         Arrays.fill(CHARS, 3784, 3790, (byte) -87 ); // Fill 6 of value (byte) -87
         Arrays.fill(CHARS, 3790, 3792, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 3792, 3802, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 3802, 3864, (byte) 33 ); // Fill 62 of value (byte) 33
         Arrays.fill(CHARS, 3864, 3866, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 3866, 3872, (byte) 33 ); // Fill 6 of value (byte) 33
         Arrays.fill(CHARS, 3872, 3882, (byte) -87 ); // Fill 10 of value (byte) -87
         Arrays.fill(CHARS, 3882, 3893, (byte) 33 ); // Fill 11 of value (byte) 33
         CHARS[3893] = -87;
         CHARS[3894] = 33;
         CHARS[3895] = -87;
         CHARS[3896] = 33;
         CHARS[3897] = -87;
         Arrays.fill(CHARS, 3898, 3902, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3902, 3904, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 3904, 3912, (byte) -19 ); // Fill 8 of value (byte) -19
         CHARS[3912] = 33;
         Arrays.fill(CHARS, 3913, 3946, (byte) -19 ); // Fill 33 of value (byte) -19
         Arrays.fill(CHARS, 3946, 3953, (byte) 33 ); // Fill 7 of value (byte) 33
         Arrays.fill(CHARS, 3953, 3973, (byte) -87 ); // Fill 20 of value (byte) -87
         CHARS[3973] = 33;
         Arrays.fill(CHARS, 3974, 3980, (byte) -87 ); // Fill 6 of value (byte) -87
         Arrays.fill(CHARS, 3980, 3984, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 3984, 3990, (byte) -87 ); // Fill 6 of value (byte) -87
         CHARS[3990] = 33;
         CHARS[3991] = -87;
         CHARS[3992] = 33;
         Arrays.fill(CHARS, 3993, 4014, (byte) -87 ); // Fill 21 of value (byte) -87
         Arrays.fill(CHARS, 4014, 4017, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 4017, 4024, (byte) -87 ); // Fill 7 of value (byte) -87
         CHARS[4024] = 33;
         CHARS[4025] = -87;
         Arrays.fill(CHARS, 4026, 4256, (byte) 33 ); // Fill 230 of value (byte) 33
         Arrays.fill(CHARS, 4256, 4294, (byte) -19 ); // Fill 38 of value (byte) -19
         Arrays.fill(CHARS, 4294, 4304, (byte) 33 ); // Fill 10 of value (byte) 33
         Arrays.fill(CHARS, 4304, 4343, (byte) -19 ); // Fill 39 of value (byte) -19
         Arrays.fill(CHARS, 4343, 4352, (byte) 33 ); // Fill 9 of value (byte) 33
         CHARS[4352] = -19;
         CHARS[4353] = 33;
         Arrays.fill(CHARS, 4354, 4356, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[4356] = 33;
         Arrays.fill(CHARS, 4357, 4360, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[4360] = 33;
         CHARS[4361] = -19;
         CHARS[4362] = 33;
         Arrays.fill(CHARS, 4363, 4365, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[4365] = 33;
         Arrays.fill(CHARS, 4366, 4371, (byte) -19 ); // Fill 5 of value (byte) -19
         Arrays.fill(CHARS, 4371, 4412, (byte) 33 ); // Fill 41 of value (byte) 33
         CHARS[4412] = -19;
         CHARS[4413] = 33;
         CHARS[4414] = -19;
         CHARS[4415] = 33;
         CHARS[4416] = -19;
         Arrays.fill(CHARS, 4417, 4428, (byte) 33 ); // Fill 11 of value (byte) 33
         CHARS[4428] = -19;
         CHARS[4429] = 33;
         CHARS[4430] = -19;
         CHARS[4431] = 33;
         CHARS[4432] = -19;
         Arrays.fill(CHARS, 4433, 4436, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 4436, 4438, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 4438, 4441, (byte) 33 ); // Fill 3 of value (byte) 33
         CHARS[4441] = -19;
         Arrays.fill(CHARS, 4442, 4447, (byte) 33 ); // Fill 5 of value (byte) 33
         Arrays.fill(CHARS, 4447, 4450, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[4450] = 33;
         CHARS[4451] = -19;
         CHARS[4452] = 33;
         CHARS[4453] = -19;
         CHARS[4454] = 33;
         CHARS[4455] = -19;
         CHARS[4456] = 33;
         CHARS[4457] = -19;
         Arrays.fill(CHARS, 4458, 4461, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 4461, 4463, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 4463, 4466, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 4466, 4468, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[4468] = 33;
         CHARS[4469] = -19;
         Arrays.fill(CHARS, 4470, 4510, (byte) 33 ); // Fill 40 of value (byte) 33
         CHARS[4510] = -19;
         Arrays.fill(CHARS, 4511, 4520, (byte) 33 ); // Fill 9 of value (byte) 33
         CHARS[4520] = -19;
         Arrays.fill(CHARS, 4521, 4523, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[4523] = -19;
         Arrays.fill(CHARS, 4524, 4526, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 4526, 4528, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 4528, 4535, (byte) 33 ); // Fill 7 of value (byte) 33
         Arrays.fill(CHARS, 4535, 4537, (byte) -19 ); // Fill 2 of value (byte) -19
         CHARS[4537] = 33;
         CHARS[4538] = -19;
         CHARS[4539] = 33;
         Arrays.fill(CHARS, 4540, 4547, (byte) -19 ); // Fill 7 of value (byte) -19
         Arrays.fill(CHARS, 4547, 4587, (byte) 33 ); // Fill 40 of value (byte) 33
         CHARS[4587] = -19;
         Arrays.fill(CHARS, 4588, 4592, (byte) 33 ); // Fill 4 of value (byte) 33
         CHARS[4592] = -19;
         Arrays.fill(CHARS, 4593, 4601, (byte) 33 ); // Fill 8 of value (byte) 33
         CHARS[4601] = -19;
         Arrays.fill(CHARS, 4602, 7680, (byte) 33 ); // Fill 3078 of value (byte) 33
         Arrays.fill(CHARS, 7680, 7836, (byte) -19 ); // Fill 156 of value (byte) -19
         Arrays.fill(CHARS, 7836, 7840, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 7840, 7930, (byte) -19 ); // Fill 90 of value (byte) -19
         Arrays.fill(CHARS, 7930, 7936, (byte) 33 ); // Fill 6 of value (byte) 33
         Arrays.fill(CHARS, 7936, 7958, (byte) -19 ); // Fill 22 of value (byte) -19
         Arrays.fill(CHARS, 7958, 7960, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 7960, 7966, (byte) -19 ); // Fill 6 of value (byte) -19
         Arrays.fill(CHARS, 7966, 7968, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 7968, 8006, (byte) -19 ); // Fill 38 of value (byte) -19
         Arrays.fill(CHARS, 8006, 8008, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 8008, 8014, (byte) -19 ); // Fill 6 of value (byte) -19
         Arrays.fill(CHARS, 8014, 8016, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 8016, 8024, (byte) -19 ); // Fill 8 of value (byte) -19
         CHARS[8024] = 33;
         CHARS[8025] = -19;
         CHARS[8026] = 33;
         CHARS[8027] = -19;
         CHARS[8028] = 33;
         CHARS[8029] = -19;
         CHARS[8030] = 33;
         Arrays.fill(CHARS, 8031, 8062, (byte) -19 ); // Fill 31 of value (byte) -19
         Arrays.fill(CHARS, 8062, 8064, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 8064, 8117, (byte) -19 ); // Fill 53 of value (byte) -19
         CHARS[8117] = 33;
         Arrays.fill(CHARS, 8118, 8125, (byte) -19 ); // Fill 7 of value (byte) -19
         CHARS[8125] = 33;
         CHARS[8126] = -19;
         Arrays.fill(CHARS, 8127, 8130, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 8130, 8133, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[8133] = 33;
         Arrays.fill(CHARS, 8134, 8141, (byte) -19 ); // Fill 7 of value (byte) -19
         Arrays.fill(CHARS, 8141, 8144, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 8144, 8148, (byte) -19 ); // Fill 4 of value (byte) -19
         Arrays.fill(CHARS, 8148, 8150, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 8150, 8156, (byte) -19 ); // Fill 6 of value (byte) -19
         Arrays.fill(CHARS, 8156, 8160, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 8160, 8173, (byte) -19 ); // Fill 13 of value (byte) -19
         Arrays.fill(CHARS, 8173, 8178, (byte) 33 ); // Fill 5 of value (byte) 33
         Arrays.fill(CHARS, 8178, 8181, (byte) -19 ); // Fill 3 of value (byte) -19
         CHARS[8181] = 33;
         Arrays.fill(CHARS, 8182, 8189, (byte) -19 ); // Fill 7 of value (byte) -19
         Arrays.fill(CHARS, 8189, 8400, (byte) 33 ); // Fill 211 of value (byte) 33
         Arrays.fill(CHARS, 8400, 8413, (byte) -87 ); // Fill 13 of value (byte) -87
         Arrays.fill(CHARS, 8413, 8417, (byte) 33 ); // Fill 4 of value (byte) 33
         CHARS[8417] = -87;
         Arrays.fill(CHARS, 8418, 8486, (byte) 33 ); // Fill 68 of value (byte) 33
         CHARS[8486] = -19;
         Arrays.fill(CHARS, 8487, 8490, (byte) 33 ); // Fill 3 of value (byte) 33
         Arrays.fill(CHARS, 8490, 8492, (byte) -19 ); // Fill 2 of value (byte) -19
         Arrays.fill(CHARS, 8492, 8494, (byte) 33 ); // Fill 2 of value (byte) 33
         CHARS[8494] = -19;
         Arrays.fill(CHARS, 8495, 8576, (byte) 33 ); // Fill 81 of value (byte) 33
         Arrays.fill(CHARS, 8576, 8579, (byte) -19 ); // Fill 3 of value (byte) -19
         Arrays.fill(CHARS, 8579, 12293, (byte) 33 ); // Fill 3714 of value (byte) 33
         CHARS[12293] = -87;
         CHARS[12294] = 33;
         CHARS[12295] = -19;
         Arrays.fill(CHARS, 12296, 12321, (byte) 33 ); // Fill 25 of value (byte) 33
         Arrays.fill(CHARS, 12321, 12330, (byte) -19 ); // Fill 9 of value (byte) -19
         Arrays.fill(CHARS, 12330, 12336, (byte) -87 ); // Fill 6 of value (byte) -87
         CHARS[12336] = 33;
         Arrays.fill(CHARS, 12337, 12342, (byte) -87 ); // Fill 5 of value (byte) -87
         Arrays.fill(CHARS, 12342, 12353, (byte) 33 ); // Fill 11 of value (byte) 33
         Arrays.fill(CHARS, 12353, 12437, (byte) -19 ); // Fill 84 of value (byte) -19
         Arrays.fill(CHARS, 12437, 12441, (byte) 33 ); // Fill 4 of value (byte) 33
         Arrays.fill(CHARS, 12441, 12443, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 12443, 12445, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 12445, 12447, (byte) -87 ); // Fill 2 of value (byte) -87
         Arrays.fill(CHARS, 12447, 12449, (byte) 33 ); // Fill 2 of value (byte) 33
         Arrays.fill(CHARS, 12449, 12539, (byte) -19 ); // Fill 90 of value (byte) -19
         CHARS[12539] = 33;
         Arrays.fill(CHARS, 12540, 12543, (byte) -87 ); // Fill 3 of value (byte) -87
         Arrays.fill(CHARS, 12543, 12549, (byte) 33 ); // Fill 6 of value (byte) 33
         Arrays.fill(CHARS, 12549, 12589, (byte) -19 ); // Fill 40 of value (byte) -19
         Arrays.fill(CHARS, 12589, 19968, (byte) 33 ); // Fill 7379 of value (byte) 33
         Arrays.fill(CHARS, 19968, 40870, (byte) -19 ); // Fill 20902 of value (byte) -19
         Arrays.fill(CHARS, 40870, 44032, (byte) 33 ); // Fill 3162 of value (byte) 33
         Arrays.fill(CHARS, 44032, 55204, (byte) -19 ); // Fill 11172 of value (byte) -19
         Arrays.fill(CHARS, 55204, 55296, (byte) 33 ); // Fill 92 of value (byte) 33
         Arrays.fill(CHARS, 57344, 65534, (byte) 33 ); // Fill 8190 of value (byte) 33

     } // <clinit>()

     //
     // Public static methods
     //

     /**
      * Returns true if the specified character is a supplemental character.
      *
      * @param c The character to check.
      */
     public static boolean isSupplemental(int c) {
         return (c >= 0x10000 && c <= 0x10FFFF);
     }

     /**
      * Returns true the supplemental character corresponding to the given
      * surrogates.
      *
      * @param h The high surrogate.
      * @param l The low surrogate.
      */
     public static int supplemental(char h, char l) {
         return (h - 0xD800) * 0x400 + (l - 0xDC00) + 0x10000;
     }

     /**
      * Returns the high surrogate of a supplemental character
      *
      * @param c The supplemental character to "split".
      */
     public static char highSurrogate(int c) {
         return (char) (((c - 0x00010000) >> 10) + 0xD800);
     }

     /**
      * Returns the low surrogate of a supplemental character
      *
      * @param c The supplemental character to "split".
      */
     public static char lowSurrogate(int c) {
         return (char) (((c - 0x00010000) & 0x3FF) + 0xDC00);
     }

     /**
      * Returns whether the given character is a high surrogate
      *
      * @param c The character to check.
      */
     public static boolean isHighSurrogate(int c) {
         return (0xD800 <= c && c <= 0xDBFF);
     }

     /**
      * Returns whether the given character is a low surrogate
      *
      * @param c The character to check.
      */
     public static boolean isLowSurrogate(int c) {
         return (0xDC00 <= c && c <= 0xDFFF);
     }


     /**
      * Returns true if the specified character is valid. This method
      * also checks the surrogate character range from 0x10000 to 0x10FFFF.
      * <p>
      * If the program chooses to apply the mask directly to the
      * <code>CHARS</code> array, then they are responsible for checking
      * the surrogate character range.
      *
      * @param c The character to check.
      */
     public static boolean isValid(int c) {
         return (c < 0x10000 && (CHARS[c] & MASK_VALID) != 0) ||
                (0x10000 <= c && c <= 0x10FFFF);
     } // isValid(int):boolean

     /**
      * Returns true if the specified character is invalid.
      *
      * @param c The character to check.
      */
     public static boolean isInvalid(int c) {
         return !isValid(c);
     } // isInvalid(int):boolean

     /**
      * Returns true if the specified character can be considered content.
      *
      * @param c The character to check.
      */
     public static boolean isContent(int c) {
         return (c < 0x10000 && (CHARS[c] & MASK_CONTENT) != 0) ||
                (0x10000 <= c && c <= 0x10FFFF);
     } // isContent(int):boolean

     /**
      * Returns true if the specified character can be considered markup.
      * Markup characters include '&lt;', '&amp;', and '%'.
      *
      * @param c The character to check.
      */
     public static boolean isMarkup(int c) {
         return c == '<' || c == '&' || c == '%';
     } // isMarkup(int):boolean

     /**
      * Returns true if the specified character is a space character
      * as defined by production [3] in the XML 1.0 specification.
      *
      * @param c The character to check.
      */
     public static boolean isSpace(int c) {
         return c <= 0x20 && (CHARS[c] & MASK_SPACE) != 0;
     } // isSpace(int):boolean

     /**
      * Returns true if the specified character is a valid name start
      * character as defined by production [5] in the XML 1.0
      * specification.
      *
      * @param c The character to check.
      */
     public static boolean isNameStart(int c) {
         return c < 0x10000 && (CHARS[c] & MASK_NAME_START) != 0;
     } // isNameStart(int):boolean

     /**
      * Returns true if the specified character is a valid name
      * character as defined by production [4] in the XML 1.0
      * specification.
      *
      * @param c The character to check.
      */
     public static boolean isName(int c) {
         return c < 0x10000 && (CHARS[c] & MASK_NAME) != 0;
     } // isName(int):boolean

     /**
      * Returns true if the specified character is a valid NCName start
      * character as defined by production [4] in Namespaces in XML
      * recommendation.
      *
      * @param c The character to check.
      */
     public static boolean isNCNameStart(int c) {
         return c < 0x10000 && (CHARS[c] & MASK_NCNAME_START) != 0;
     } // isNCNameStart(int):boolean

     /**
      * Returns true if the specified character is a valid NCName
      * character as defined by production [5] in Namespaces in XML
      * recommendation.
      *
      * @param c The character to check.
      */
     public static boolean isNCName(int c) {
         return c < 0x10000 && (CHARS[c] & MASK_NCNAME) != 0;
     } // isNCName(int):boolean

     /**
      * Returns true if the specified character is a valid Pubid
      * character as defined by production [13] in the XML 1.0
      * specification.
      *
      * @param c The character to check.
      */
     public static boolean isPubid(int c) {
         return c < 0x10000 && (CHARS[c] & MASK_PUBID) != 0;
     } // isPubid(int):boolean

     /*
      * [5] Name ::= (Letter | '_' | ':') (NameChar)*
      */
     /**
      * Check to see if a string is a valid Name according to [5]
      * in the XML 1.0 Recommendation
      *
      * @param name string to check
      * @return true if name is a valid Name
      */
     public static boolean isValidName(String name) {
         if (name.length() == 0)
             return false;
         char ch = name.charAt(0);
         if( isNameStart(ch) == false)
            return false;
         for (int i = 1; i < name.length(); i++ ) {
            ch = name.charAt(i);
            if( isName( ch ) == false ){
               return false;
            }
         }
         return true;
     } // isValidName(String):boolean
     

     /*
      * from the namespace rec
      * [4] NCName ::= (Letter | '_') (NCNameChar)*
      */
     /**
      * Check to see if a string is a valid NCName according to [4]
      * from the XML Namespaces 1.0 Recommendation
      *
      * @param ncName string to check
      * @return true if name is a valid NCName
      */
     public static boolean isValidNCName(String ncName) {
         if (ncName.length() == 0)
             return false;
         char ch = ncName.charAt(0);
         if( isNCNameStart(ch) == false)
            return false;
         for (int i = 1; i < ncName.length(); i++ ) {
            ch = ncName.charAt(i);
            if( isNCName( ch ) == false ){
               return false;
            }
         }
         return true;
     } // isValidNCName(String):boolean

     /*
      * [7] Nmtoken ::= (NameChar)+
      */
     /**
      * Check to see if a string is a valid Nmtoken according to [7]
      * in the XML 1.0 Recommendation
      *
      * @param nmtoken string to check
      * @return true if nmtoken is a valid Nmtoken 
      */
     public static boolean isValidNmtoken(String nmtoken) {
         if (nmtoken.length() == 0)
             return false;
         for (int i = 0; i < nmtoken.length(); i++ ) {
            char ch = nmtoken.charAt(i);
            if(  ! isName( ch ) ){
               return false;
            }
         }
         return true;
     } // isValidName(String):boolean





     // encodings

     /**
      * Returns true if the encoding name is a valid IANA encoding.
      * This method does not verify that there is a decoder available
      * for this encoding, only that the characters are valid for an
      * IANA encoding name.
      *
      * @param ianaEncoding The IANA encoding name.
      */
     public static boolean isValidIANAEncoding(String ianaEncoding) {
         if (ianaEncoding != null) {
             int length = ianaEncoding.length();
             if (length > 0) {
                 char c = ianaEncoding.charAt(0);
                 if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                     for (int i = 1; i < length; i++) {
                         c = ianaEncoding.charAt(i);
                         if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') &&
                             (c < '0' || c > '9') && c != '.' && c != '_' &&
                             c != '-') {
                             return false;
                         }
                     }
                     return true;
                 }
             }
         }
         return false;
     } // isValidIANAEncoding(String):boolean

     /**
      * Returns true if the encoding name is a valid Java encoding.
      * This method does not verify that there is a decoder available
      * for this encoding, only that the characters are valid for an
      * Java encoding name.
      *
      * @param javaEncoding The Java encoding name.
      */
     public static boolean isValidJavaEncoding(String javaEncoding) {
         if (javaEncoding != null) {
             int length = javaEncoding.length();
             if (length > 0) {
                 for (int i = 1; i < length; i++) {
                     char c = javaEncoding.charAt(i);
                     if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') &&
                         (c < '0' || c > '9') && c != '.' && c != '_' &&
                         c != '-') {
                         return false;
                     }
                 }
                 return true;
             }
         }
         return false;
     } // isValidIANAEncoding(String):boolean


 } // class XMLChar

 public static class TypeValidator {

   //order constants
   public static final short LESS_THAN     = -1;
   public static final short EQUAL         = 0;
   public static final short GREATER_THAN  = 1;
   public static final short INDETERMINATE = 2;


   // check whether the character is in the range 0x30 ~ 0x39
   public static final boolean isDigit(char ch) {
       return ch >= '0' && ch <= '9';
   }
   
   // if the character is in the range 0x30 ~ 0x39, return its int value (0~9),
   // otherwise, return -1
   public static final int getDigit(char ch) {
       return isDigit(ch) ? ch - '0' : -1;
   }
   
} // interface TypeValidator

}
