package edu.northeastern.new_final.ui.profile;

import edu.northeastern.new_final.CircularImageView;
import edu.northeastern.new_final.R;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import edu.northeastern.new_final.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FragmentContainerView fragmentContainer;
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 1;

    private static final int PERMISSIONS_REQUEST_CAMERA = 2;

    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private static final int PICK_IMAGE = 123;
    String username;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Retrieve email from shared preferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        // Set email to the textViewUsername
        TextView textViewUsername = binding.textViewUsername;
        textViewUsername.setText(email);

        //Section helps keep garden image at 2/3 of screen height (shows on emulator)
        // Get display metrics to calculate screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Calculate height of the image to be two-thirds of the screen height
        int imageHeight = (int) (screenHeight * 0.55);

        // Get reference to the ImageView and adjust its height
        ImageView imageView = binding.imageViewGarden;
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = imageHeight;
        imageView.setLayoutParams(layoutParams);


        //Section for profile sub-fragments (Home, My Groups, Workout History)
        // Find fragment container
        fragmentContainer = root.findViewById(R.id.fragment_container);

        // Set initial fragment
        loadFragment(new HomeFragment());

        // Set onClickListeners for bottom bar nav buttons. Programmatically change button color.
        Button buttonHome = root.findViewById(R.id.button_home);
        Button buttonGroups = root.findViewById(R.id.button_groups);
        Button buttonWorkouts = root.findViewById(R.id.button_workouts);

        buttonHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            buttonHome.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple_pastel));
            buttonGroups.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
            buttonWorkouts.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
        });

        buttonGroups.setOnClickListener(v -> {
            loadFragment(new MyGroupsFragment());
            buttonHome.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
            buttonGroups.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple_pastel));
            buttonWorkouts.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
        });

        buttonWorkouts.setOnClickListener(v -> {
            loadFragment(new WorkoutHistoryFragment());
            buttonHome.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
            buttonGroups.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white));
            buttonWorkouts.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple_pastel));
        });

        loadUserProfileImage();

        CircularImageView profileImageView = binding.imageViewProfile;
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to open the image selector
                openImageSelector();
            }
        });


        username = sharedPreferences.getString("email", null);
        String sanitizedUsername = username.replace(".", "_");

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userEPRef = databaseRef.child("users").child(sanitizedUsername).child("total_EP");

        userEPRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ProfileFragment", "11111111");
                if (dataSnapshot.exists()) {
                    Log.d("ProfileFragment", "2222222222");
                    Integer totalEP = dataSnapshot.getValue(Integer.class);
                    if (totalEP != null) {
                        Log.d("ProfileFragment", "total_EP: " + totalEP); // Debugging log
                        // Using ViewBinding to find the ImageView
                        ImageView imageViewGarden = binding.imageViewGarden;
                        if (totalEP >= 0 && totalEP <= 30) {
                            imageViewGarden.setImageResource(R.drawable.stage1);
                        } else if (totalEP > 30 && totalEP <= 100) {
                            imageViewGarden.setImageResource(R.drawable.stage2);
                        } else if (totalEP > 100 && totalEP <= 300) {
                            imageViewGarden.setImageResource(R.drawable.stage3);
                        } else if (totalEP > 300) {
                            imageViewGarden.setImageResource(R.drawable.stage4);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error case
                Log.w("TAG", "loadTotalEP:onCancelled", databaseError.toException());
            }

            CircularImageView profileImageView = binding.imageViewProfile;

        });



        return root;
    }
    private void loadUserProfileImage() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String sanitizedEmail = email.replace(".", "_");

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference userRef = databaseRef.child(sanitizedEmail).child("profileImageUrl");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageUrl = dataSnapshot.getValue(String.class);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        // Use Glide to load the image
                        Glide.with(ProfileFragment.this)
                                .load(profileImageUrl)
                                .into(binding.imageViewProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ProfileFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri != null) {
            String userId = username.replace(".", "_");

            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profile_images/" + userId + ".jpg");

            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    Log.d("ProfileFragment", "Image upload successful. URL: " + downloadUri.toString());

                                    // Update the user's profile in the Realtime Database with the image URL
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                    userRef.child("profileImageUrl").setValue(downloadUri.toString())
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Log.d("ProfileFragment", "User profile updated with new image URL.");

                                                    // Set the image in the ImageView using Glide or similar library
                                                    Glide.with(ProfileFragment.this)
                                                            .load(downloadUri.toString())
                                                            .into(binding.imageViewProfile);
                                                } else {
                                                    Log.e("ProfileFragment", "Failed to update user profile image URL.", task.getException());
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("ProfileFragment", "Image upload failed: " + exception.getMessage());
                        }
                    });
        }
    }





    private void openImageSelector() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                    } else {
                        takePhoto();
                    }
                } else if (items[item].equals("Choose from Gallery")) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_STORAGE);
                    } else {
                        selectImageFromGallery();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the image selector.
                selectImage();
            } else {
                // Permission denied, show an explanatory message or UI.
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = null;
            if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap photo = (Bitmap) extras.get("data");
                imageUri = getImageUri(getContext(), photo);
            }

            if (imageUri != null) {
                // Upload the image to Firebase Storage or another service
                uploadImageToFirebaseStorage(imageUri);
            }
        }
    }
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void loadFragment(Fragment fragment) {
        Log.d("ProfileFragment", "Loading fragment: " + fragment.getClass().getSimpleName());
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
        Log.d("ProfileFragment", "Fragment loaded successfully");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

