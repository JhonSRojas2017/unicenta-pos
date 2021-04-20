//    KrOS POS  - Open Source Point Of Sale
//    Copyright (c) 2009-2018 uniCenta & previous Openbravo POS works
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.data.loader;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JG uniCenta
 */
public class ImageUtils {

    private final static Logger LOGGER = Logger.getLogger(ImageUtils.class.getName());
    private final static char[] HEXCHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Creates a new instance of ImageUtils
     */
    private ImageUtils() {
    }

    private static byte[] readStream(InputStream inputstream) throws IOException {
        //TODO improve null checking Objects.requireNonNull(inputstream, "inputstream should not be null");
        byte[] buffer = new byte[1024];
        byte[] resource = new byte[0];
        int n;

        if (inputstream != null) {
            while ((n = inputstream.read(buffer)) != -1) {
                byte[] b = new byte[resource.length + n];
                System.arraycopy(resource, 0, b, 0, resource.length);
                System.arraycopy(buffer, 0, b, resource.length, n);
                resource = b;
            }
        }
        return resource;
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static byte[] getBytesFromResource(String filePath) {

        byte[] image = new byte[0];
        if (filePath != null) {
            //TODO improve null checking Objects.requireNonNull(filePath, "filePath should not be null");
            InputStream in = ImageUtils.class.getResourceAsStream(filePath);

            try {
                return ImageUtils.readStream(in);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "getBytesFromResource", e);
            }
        }
        return image;

    }

    /**
     *
     * @param urlString
     * @return
     */
    public static BufferedImage readImage(String urlString) {
        //TODO improve null checking Objects.requireNonNull(urlString, "urlString should not be null");
        BufferedImage image = generateImage();;
        try {
            image = readImage(new URL(urlString));
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, "ReadImage from string url: "+urlString, ex);
        }
        return image;
    }

    /**
     *
     * @param url
     * @return
     */
    public static BufferedImage readImage(URL url) {
        //TODO improve null checking Objects.requireNonNull(url, "url should not be null");
        BufferedImage image = generateImage();
        if (url != null) {
            try {
                URLConnection urlConnection = url.openConnection();
                try (InputStream in = urlConnection.getInputStream()) {
                    image = readImage(readStream(in));
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "ReadImage from url: "+url, ex);
            }
        }
        return image;
    }

    /**
     *
     * @param imageByteArray
     * @return
     */
    public static BufferedImage readImage(byte[] imageByteArray) {
        //TODO improve null checking Objects.requireNonNull(imageByteArray, "imageByteArray should not be null");
        BufferedImage image = generateImage();
        if (imageByteArray != null) {
            try (ByteArrayInputStream input = new ByteArrayInputStream(imageByteArray)) {
                image = ImageIO.read(input);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "ReadImage from byte array", ex);
            }
        }
        return image;
    }

    /**
     *
     * @param img
     * @return
     */
    public static byte[] writeImage(BufferedImage img) {
        //TODO improve null checking Objects.requireNonNull(img, "img should not be null");
        byte[] imageByte = new byte[0];
        if (img != null) {
            try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();) {
                ImageIO.write(img, "png", byteOutputStream);
                byteOutputStream.flush();
                byteOutputStream.close();
                imageByte = byteOutputStream.toByteArray();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Write Image to byte array", ex);
            }
        }
        return imageByte;
    }

    /**
     *
     * @param objectByteArrary
     * @return
     */
    public static Object readSerializable(byte[] objectByteArrary) {
        //TODO improve null checking Objects.requireNonNull(objectByteArrary, "objectByteArrary should not be null");
        Object obj = null;
        if (objectByteArrary != null) {
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(objectByteArrary))) {
                obj = in.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "readSerializable", ex);
            }
        }
        return obj;
    }

    /**
     *
     * @param obj
     * @return
     */
    public static byte[] writeSerializable(Object obj) {
        //TODO improve null checking Objects.requireNonNull(obj, "obj should not be null");
        byte[] objectSerialize = new byte[0];
        if (obj != null) {
            try {
                ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
                try (ObjectOutputStream out = new ObjectOutputStream(bOutput)) {
                    out.writeObject(obj);
                    out.flush();
                    objectSerialize = bOutput.toByteArray();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "writeSerializable", ex);
            }
        }
        return objectSerialize;
    }

    /**
     *
     * @param propByte
     * @return
     */
    public static Properties readProperties(byte propByte[]) {
        //TODO improve null checking Objects.requireNonNull(propByte, "propByte should not be null");
        Properties prop = new Properties();
        if (propByte != null) {
            try {
                prop.loadFromXML(new ByteArrayInputStream(propByte));
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "readProperties", ex);
            }
        }
        return prop;
    }

    /**
     *
     * @param binput
     * @return
     */
    public static String bytes2hex(byte[] binput) {

        String result = "";
        if (binput != null) {
            StringBuilder s = new StringBuilder(binput.length * 2);
            for (int i = 0; i < binput.length; i++) {
                byte b = binput[i];
                s.append(HEXCHARS[(b & 0xF0) >> 4]);
                s.append(HEXCHARS[b & 0x0F]);
            }
            
            result = s.toString();
        }
        return result;
    }

    public static BufferedImage generateImage(int width, int heigth) {
        return new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
    }

    private static BufferedImage generateImage() {
        return null; //generateImage(1, 1);
    }
}
