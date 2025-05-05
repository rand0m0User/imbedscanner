package imbedscanner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import pdqhashing.tools.*;
import pdqhashing.types.Hash256;

public class Main {

    public static void main(String[] args) throws Exception {

        //create the output folder if it does not exist
        Path root = Paths.get(".\\");
        Path outdir = root.resolve("output");
        if (!Files.isDirectory(outdir)) {
            Files.createDirectories(outdir);
        }
        Path path = Paths.get(args[0]);

        //dont re process your output
        if (!path.getParent().endsWith("output")) {
            System.out.println("processing file: " + args[0]);
            //hash the orignal image
            BufferedImage originalImage = ImageIO.read(path.toFile());
            String originalhashstr = JustGiveMeThePDQ.execute(originalImage);
            Hash256 originalhash = Hash256.fromHexString(originalhashstr);

            //hide all other bits than bit zero and compute a second hash
            BufferedImage maskedImage = shiftbits(6, hidebits(1, originalImage));
            String maskedstr = JustGiveMeThePDQ.execute(maskedImage);
            Hash256 masked = Hash256.fromHexString(maskedstr);

            //compare both hashes using a hammingDistance (fancy bit count)
            int dist = masked.hammingDistance(originalhash);
            if (dist >= 100) { // tolerance
                System.out.println("file: " + path + " may contain hidden content, please reveiw manually!");
                System.out.println("hash: " + originalhashstr + ", dist: " + dist + ", hash2: " + maskedstr);
                //save the image for human reveiw 
                ImageIO.write(maskedImage, "png", new File(".\\output\\output_" + path.getFileName()));
            } else {
                System.out.println("processed file: " + path + ", dist: " + dist);
                //System.out.println("hash: " + originalhash + ", dist: " + dist + ", hash2: " + maskedstr);
            }
        }
    }

    public static BufferedImage hidebits(int mask, BufferedImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB value of the pixel
                int rgb = input.getRGB(x, y);
                // Extract the individual color components (ARGB)
                int alpha = (rgb >> 24) & 0xFF; // Alpha is not XORed
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                // AND each color component
                red = (red & mask);
                green = (green & mask);
                blue = (blue & mask);
                // Ensure the values are within the valid range (0-255) - important!
                red = Math.max(0, Math.min(255, red)); // Clamp to 0-255
                green = Math.max(0, Math.min(255, green)); // Clamp to 0-255
                blue = Math.max(0, Math.min(255, blue)); // Clamp to 0-255
                // Reconstruct the RGB value
                int newRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                // Set the pixel in the new image
                input.setRGB(x, y, newRgb);
            }
        }
        return input;
    }

    public static BufferedImage shiftbits(int amount, BufferedImage input) {
        int width = input.getWidth();
        int height = input.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB value of the pixel
                int rgb = input.getRGB(x, y);
                // Extract the individual color components (ARGB)
                int alpha = (rgb >> 24) & 0xFF; // Alpha is not XORed
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                //multiply each color component
                red = (red << amount);
                green = (green << amount);
                blue = (blue << amount);
                // Reconstruct the RGB value
                int newRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                // Set the pixel in the new image
                input.setRGB(x, y, newRgb);
            }
        }
        return input;
    }
}
