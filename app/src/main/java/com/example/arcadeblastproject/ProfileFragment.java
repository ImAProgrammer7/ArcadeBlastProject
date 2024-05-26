package com.example.arcadeblastproject;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int GALLERY_REQ_CODE = 1000;
    private final int CAMERA_REQ_CODE = 2000;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView firstNameTV;
    private TextView lastNameTV;
    private TextView usernameTV;
    private TextView snakeHighScoreTV;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private CircleImageView profilePicture;
    private Uri profilePictureUri;
    private ConstraintLayout constraintLayout;
    private Button camera;
    private Button gallery;
    private TextView survivalHighScoreTV;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firstNameTV = getView().findViewById(R.id.firstNameField);
        lastNameTV = getView().findViewById(R.id.lastNameField);
        usernameTV = getView().findViewById(R.id.usernameField);
        snakeHighScoreTV = getView().findViewById(R.id.snakeHighScoreField);
        survivalHighScoreTV = getView().findViewById(R.id.survivalHighScoreField);
        profilePicture = getView().findViewById(R.id.profilePicture);
        profilePicture.setOnClickListener(this);

        db.collection("users").document(Login.getActiveUserName()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    String firstName = value.get("firstName", String.class);
                    String lastName = value.get("lastName", String.class);
                    String username = value.get("username", String.class);
                    int highestScore = Objects.requireNonNull(value.get("highestScore", Integer.class));
                    int survivalHighestScore = Objects.requireNonNull(value.get("survivalHighScore", Integer.class));

                    String picture = value.get("profilePic", String.class);

                    try {
                        profilePicture.setBackgroundResource(Integer.parseInt(picture));
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    firstNameTV.setText("First Name: " + firstName);
                    lastNameTV.setText("Last Name: " + lastName);
                    usernameTV.setText("Username: " + username);
                    snakeHighScoreTV.setText("Snake Highest Score: " + String.valueOf(highestScore));
                    survivalHighScoreTV.setText("Survival Highest Score: " + String.valueOf(survivalHighestScore));
                }
            }
        });

        StorageReference imagesReference = storageReference.child(Login.getActiveUserName() + "/profilePicture");

        final int MAX_BYTES = 1024 * 1024;

        imagesReference.getBytes(MAX_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePicture.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void displayBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        View layout = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout, constraintLayout, false);

        constraintLayout = layout.findViewById(R.id.idRLBottomSheet);
        camera = constraintLayout.findViewById(R.id.idTVCamera);
        gallery = constraintLayout.findViewById(R.id.idTVGallery);

        // passing our layout file to our bottom sheet dialog.
        bottomSheetDialog.setContentView(layout);

        // below line is to set our bottom sheet dialog as cancelable.
        bottomSheetDialog.setCancelable(false);

        // below line is to set our bottom sheet cancelable.
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        // below line is to display our bottom sheet dialog.
        bottomSheetDialog.show();
        
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                }
                startActivityForResult(intent, CAMERA_REQ_CODE);
                bottomSheetDialog.dismiss();

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                //Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, GALLERY_REQ_CODE);
                bottomSheetDialog.dismiss();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE && data != null && data.getData() != null) {
            profilePictureUri = data.getData();
            profilePicture.setImageURI(profilePictureUri);
            db.collection("users").document(Login.getActiveUserName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null){
                        User u = documentSnapshot.toObject(User.class);
                        if (u != null) {
                            uploadPicture(u);
                        }
                    }
                }
            });
        }


        if(resultCode == RESULT_OK && requestCode == CAMERA_REQ_CODE && data != null && data.getExtras() != null) {
            Bundle extras = data.getExtras();
            Bitmap profilePictureBitmap = (Bitmap) extras.get("data");
            profilePictureUri = getImageUri(requireContext(), profilePictureBitmap);

            profilePicture.setImageBitmap(profilePictureBitmap);
            db.collection("users").document(Login.getActiveUserName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null){
                        User u = documentSnapshot.toObject(User.class);
                        if (u != null) {
                            uploadPicture(u);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        displayBottomSheet();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void uploadPicture(User user) {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading image...");
        progressDialog.show();

        StorageReference imagesReference = storageReference.child(user.getUsername() + "/profilePicture");

        imagesReference.putFile(profilePictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred()) /
                        snapshot.getTotalByteCount();


                progressDialog.setMessage("Percentage: " + (int) progressPercentage + "%");
            }
        });
    }
}