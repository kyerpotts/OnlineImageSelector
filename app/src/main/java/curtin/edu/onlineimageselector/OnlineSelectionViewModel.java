package curtin.edu.onlineimageselector;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

public class OnlineSelectionViewModel extends ViewModel {
    private Bitmap selectedImage;

    public Bitmap getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Bitmap selectedImage) {
        this.selectedImage = selectedImage;
    }
}
