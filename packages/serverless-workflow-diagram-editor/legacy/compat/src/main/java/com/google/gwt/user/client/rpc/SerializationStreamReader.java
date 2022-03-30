//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.google.gwt.user.client.rpc;

public interface SerializationStreamReader {
    boolean readBoolean() throws SerializationException;

    byte readByte() throws SerializationException;

    char readChar() throws SerializationException;

    double readDouble() throws SerializationException;

    float readFloat() throws SerializationException;

    int readInt() throws SerializationException;

    long readLong() throws SerializationException;

    Object readObject() throws SerializationException;

    short readShort() throws SerializationException;

    String readString() throws SerializationException;
}
