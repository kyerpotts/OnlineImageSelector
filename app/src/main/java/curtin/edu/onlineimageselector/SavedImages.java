package curtin.edu.onlineimageselector;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SavedImages extends Fragment {
    List<String> savedImagesList;
    RecyclerView rvSavedImages;
    Button btnBackToImageSelection;
    StorageReference storageReference;
    SavedImageAdapter adapter;
    ProgressBar pbRetrieveImages;

    public SavedImages() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_images, container, false);

        // Set up for recycler view
        savedImagesList = new ArrayList<>();
        rvSavedImages = view.findViewById(R.id.rvSavedImages);
        adapter = new SavedImageAdapter();
        rvSavedImages.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set up progress bar
        pbRetrieveImages = view.findViewById(R.id.pbRetrieveImages);
        pbRetrieveImages.setVisibility(View.VISIBLE);

        // Set up return button
        btnBackToImageSelection = view.findViewById(R.id.btnBackToImageSelection);
        btnBackToImageSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(SavedImagesDirections.actionSavedImagesToOnlineSelection());
            }
        });

        // Set up to retrieve data from Firebase.
        // Code referenced from https://www.geeksforgeeks.org/how-to-view-all-the-uploaded-images-in-firebase-storage/?ref=rp
        StorageReference listReference = FirebaseStorage.getInstance().getReference().child("images/");
        listReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference image : listResult.getItems()) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            savedImagesList.add(uri.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            adapter.setData(savedImagesList);
                            rvSavedImages.setAdapter(adapter);
                            pbRetrieveImages.setVisibility(View.INVISIBLE);
                            Toast.makeText(requireActivity(), "Successfully retrieved Images", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pbRetrieveImages.setVisibility(View.INVISIBLE);
                            Toast.makeText(requireActivity(), "Image retrieval unsuccessful: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return view;
    }
}