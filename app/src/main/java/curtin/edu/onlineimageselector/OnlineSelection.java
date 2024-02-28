package curtin.edu.onlineimageselector;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import curtin.edu.onlineimageselector.connectionutils.ImageRetrievalTask;
import curtin.edu.onlineimageselector.connectionutils.SearchTask;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OnlineSelection extends Fragment {

    // Private fields for views;
    private OnlineSelectionViewModel onlineSelectionViewModel;
    private EditText etSearch;
    private Button btnSearch;
    private Button btnSingle;
    private Button btnMulti;
    private RecyclerView rvOnlineImages;
    private Button btnUpload;
    private Button btnViewSavedImages;
    private LinearLayoutManager singleLayout;
    private GridLayoutManager multiLayout;
    private OnlineImageAdapter adapter;
    private ProgressBar pbSearch;

    // Fields for Firebase storage
    FirebaseStorage storage;
    StorageReference storageReference;

    public OnlineSelection() {
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
        View view = inflater.inflate(R.layout.fragment_online_selection, container, false);

        // Set up connection to firebase database
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        // Instantiate view-model, using ViewModelProvider ensures that the view-model is attached to the activity and is not destroyed when fragment changes
        onlineSelectionViewModel = new ViewModelProvider(requireActivity()).get(OnlineSelectionViewModel.class);

        // Instantiate required views
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSingle = view.findViewById(R.id.btnSingle);
        btnMulti = view.findViewById(R.id.btnMulti);
        rvOnlineImages = view.findViewById(R.id.rvOnlineImages);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnViewSavedImages = view.findViewById(R.id.btnViewSavedImages);
        pbSearch = view.findViewById(R.id.pbSearch);

        // Set up RecyclerView dependencies
        singleLayout = new LinearLayoutManager(getActivity());
        multiLayout = new GridLayoutManager(getActivity(), 2);
        adapter = new OnlineImageAdapter();
        adapter.setOnlineSelectionViewModel(onlineSelectionViewModel);
        rvOnlineImages.setLayoutManager(singleLayout);
        rvOnlineImages.setAdapter(adapter);


        // Set up clickListeners for button views
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchImage();
            }
        });

        btnSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSingle();
            }
        });

        btnMulti.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View view) {
                setMulti();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        btnViewSavedImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(OnlineSelectionDirections.actionOnlineSelectionToSavedImages());
            }
        });

        return view;
    }

    // Set up to upload data to Firebase.
    // Code referenced from https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/?ref=rp
    private void uploadImage() {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        Bitmap bitmap = onlineSelectionViewModel.getSelectedImage();
        File file = new File(requireContext().getCacheDir(), "bitmap.jpeg");
        Uri uri;
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            uri = Uri.fromFile(file);
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(requireActivity(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                            Toast.makeText(requireActivity(), "Failed uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchImage() {
        pbSearch.setVisibility(View.VISIBLE);
        SearchTask searchTask = new SearchTask(requireActivity());
        searchTask.setSearchkey(etSearch.getText().toString());
        Single<String> searchObservable = Single.fromCallable(searchTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());
        searchObservable.subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                pbSearch.setVisibility(View.INVISIBLE);
                loadImages(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                pbSearch.setVisibility(View.INVISIBLE);
                Toast.makeText(requireActivity(), "Searching Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadImages(String s) {
        ImageRetrievalTask imageRetrievalTask = new ImageRetrievalTask(requireActivity());
        imageRetrievalTask.setData(s);
        pbSearch.setVisibility(View.VISIBLE);
        Single<List<Bitmap>> searchObservable = Single.fromCallable(imageRetrievalTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());
        searchObservable.subscribe(new SingleObserver<List<Bitmap>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull List<Bitmap> bitmaps) {
                pbSearch.setVisibility(View.INVISIBLE);
                adapter.setData(bitmaps);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(requireActivity(), "Image loading error, search again", Toast.LENGTH_SHORT).show();
                pbSearch.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setSingle() {
        rvOnlineImages.setLayoutManager(singleLayout);
    }

    private void setMulti() {
        rvOnlineImages.setLayoutManager(multiLayout);
    }
}