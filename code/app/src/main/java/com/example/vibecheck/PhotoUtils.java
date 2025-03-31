/**
 * This is a small utility class for handling image selection, encoding, and decoding.
 * Functions include launching an image picker activity to select and handle a user's image,
 * and a function to convert an image stream to a byte array.
 *
 * This class has no outstanding issues
 */

package com.example.vibecheck;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class PhotoUtils {

    /**
     * Converts an image as an input stream to a byte array.
     * Method was adapted from examples found in stackoverflow:
     * https://stackoverflow.com/questions/1264709/convert-inputstream-to-byte-array-in-java
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] convertStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * Launches an image picker activity so the user can select an image from their device.
     * Handles the selected image by converting teh image stream to a byte array, and encodes it to a string
     * to be used as the image data for the mood event.
     * @param activity
     * @param imagePreview
     * @param removeButton
     * @param onImageEncoded
     * @return
     *      Returns an ActivityResultLauncher for launching the image picker activity.
     */
    public static ActivityResultLauncher<Intent> createImagePickerLauncher(
            AppCompatActivity activity,
            ImageView imagePreview,
            View removeButton,
            Consumer<String> onImageEncoded
    ) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        imagePreview.setImageURI(selectedImageUri);

                        try {
                            InputStream inputStream = activity.getContentResolver().openInputStream(selectedImageUri);
                            byte[] byteArray = convertStreamToByteArray(inputStream);

                            // Check if the image size is within the limit
                            if (byteArray.length > 65536) {
                                Toast.makeText(activity, "Image is too large! Max size: 65536 bytes.", Toast.LENGTH_SHORT).show();
                                imagePreview.setImageResource(R.drawable.add_post_icon);
                                removeButton.setVisibility(View.GONE);
                                onImageEncoded.accept(null);
                                return;
                            }

                            //Set views to visible and encode image to string
                            imagePreview.setVisibility(View.VISIBLE);
                            removeButton.setVisibility(View.VISIBLE);
                            onImageEncoded.accept(Base64.encodeToString(byteArray, Base64.DEFAULT));

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Failed to convert image", Toast.LENGTH_SHORT).show();
                            onImageEncoded.accept(null);
                        }
                    }
                }
        );
    }
}