package taowu.core.config.common.util;

import java.io.*;

/**
 * Created by  on 2016/10/20.
 */
public class CodeUtils {

    /**
     *
     */
    public static final String PREFERRED_ENCODING = "UTF-8";

    /**
     *
     * @param chars
     * @return
     */
    public static byte[] toBytes(char[] chars) {
        return toBytes(new String(chars), PREFERRED_ENCODING);
    }

    /**
     *
     * @param chars
     * @param encoding
     * @return
     * @throws CodecException
     */
    public static byte[] toBytes(char[] chars, String encoding) throws CodecException {
        return toBytes(new String(chars), encoding);
    }

    /**
     *
     * @param source
     * @return
     */
    public static byte[] toBytes(String source) {
        return toBytes(source, PREFERRED_ENCODING);
    }

    /**
     *
     * @param source
     * @param encoding
     * @return
     * @throws CodecException
     */
    public static byte[] toBytes(String source, String encoding) throws CodecException {
        try {
            return source.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            String msg = "Unable to convert source [" + source + "] to byte array using " +
                    "encoding '" + encoding + "'";
            throw new CodecException(msg, e);
        }
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static String toString(byte[] bytes) {
        return toString(bytes, PREFERRED_ENCODING);
    }

    /**
     *
     * @param bytes
     * @param encoding
     * @return
     * @throws CodecException
     */
    public static String toString(byte[] bytes, String encoding) throws CodecException {
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            String msg = "Unable to convert byte array to String with encoding '" + encoding + "'.";
            throw new CodecException(msg, e);
        }
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static char[] toChars(byte[] bytes) {
        return toChars(bytes, PREFERRED_ENCODING);
    }

    /**
     *
     * @param bytes
     * @param encoding
     * @return
     * @throws CodecException
     */
    public static char[] toChars(byte[] bytes, String encoding) throws CodecException {
        return toString(bytes, encoding).toCharArray();
    }

    /**
     * Returns {@code true} if the specified object can be easily converted to bytes by instances of this class,
     * {@code false} otherwise.
     * <p/>
     * The default implementation returns {@code true} IFF the specified object is an instance of one of the following
     * types:
     * <ul>
     * <li>{@code byte[]}</li>
     * <li>{@code char[]}</li>
     * <li>{@link String}</li>
     * <li>{@link File}</li>
     * </li>{@link InputStream}</li>
     * </ul>
     *
     * @param o the object to test to see if it can be easily converted to a byte array
     * @return {@code true} if the specified object can be easily converted to bytes by instances of this class,
     *         {@code false} otherwise.
     * @since 1.0
     */
    public static  boolean isByteSource(Object o) {
        return o instanceof byte[] || o instanceof char[] || o instanceof String ||
                o instanceof File || o instanceof InputStream;
    }

    /**
     *
     * @param o
     * @return
     */
    public static  byte[] toBytes(Object o) {
        if (o == null) {
            String msg = "Argument for byte conversion cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        if (o instanceof byte[]) {
            return (byte[]) o;
        } else if (o instanceof char[]) {
            return toBytes((char[]) o);
        } else if (o instanceof String) {
            return toBytes((String) o);
        } else if (o instanceof File) {
            return toBytes((File) o);
        } else if (o instanceof InputStream) {
            return toBytes((InputStream) o);
        } else {
            return objectToBytes(o);
        }
    }

    /**
     * Converts the specified Object into a String.
     * <p/>
     * If the argument is a {@code byte[]} or {@code char[]} it will be converted to a String using the
     * {@link #PREFERRED_ENCODING}.  If a String, it will be returned as is.
     * <p/>
     * If the argument is anything other than these three types, it is passed to the
     * {@link #objectToString(Object) objectToString} method.
     *
     * @param o the Object to convert into a byte array
     * @return a byte array representation of the Object argument.
     */
    public static  String toString(Object o) {
        if (o == null) {
            String msg = "Argument for String conversion cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        if (o instanceof byte[]) {
            return toString((byte[]) o);
        } else if (o instanceof char[]) {
            return new String((char[]) o);
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return objectToString(o);
        }
    }

    public static  byte[] toBytes(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File argument cannot be null.");
        }
        try {
            return toBytes(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            String msg = "Unable to acquire InputStream for file [" + file + "]";
            throw new CodecException(msg, e);
        }
    }

    /**
     * Converts the specified {@link InputStream InputStream} into a byte array.
     *
     * @param in the InputStream to convert to a byte array
     * @return the bytes of the input stream
     * @throws IllegalArgumentException if the {@code InputStream} argument is {@code null}.
     * @throws CodecException           if there is any problem reading from the {@link InputStream}.
     * @since 1.0
     */
    public static  byte[] toBytes(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("InputStream argument cannot be null.");
        }
        final int BUFFER_SIZE = 512;
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        } catch (IOException ioe) {
            throw new CodecException(ioe);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     *
     * @param o
     * @return
     */
    public static  byte[] objectToBytes(Object o) {
        String msg = "The  implementation only supports conversion to " +
                "byte[] if the source is of type byte[], char[], String, " + CodeUtils.class.getName() +
                " File or InputStream.  The instance provided as a method " +
                "argument is of type [" + o.getClass().getName() + "].  If you would like to convert " +
                "this argument type to a byte[], you can 1) convert the argument to one of the supported types ";
        throw new CodecException(msg);
    }

    /**
     *
     * @param o
     * @return
     */
    public static  String objectToString(Object o) {
        return o.toString();
    }

    public static  class CodecException extends RuntimeException{
        public CodecException() {
            super();
        }

        public CodecException(String message) {
            super(message);
        }

        public CodecException(Throwable cause) {
            super(cause);
        }

        public CodecException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
