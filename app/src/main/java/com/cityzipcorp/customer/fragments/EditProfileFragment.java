package com.cityzipcorp.customer.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.UserCallback;
import com.cityzipcorp.customer.model.User;
import com.cityzipcorp.customer.store.UserStore;
import com.cityzipcorp.customer.utils.ChoosePhoto;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.Utils;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by anilpathak on 16/11/17.
 */

public class EditProfileFragment extends BaseFragment {

    @BindView(R.id.img_profile)
    ImageView imgProfile;
    @BindView(R.id.txt_upload)
    TextView txtUpload;
    @BindView(R.id.edt_first_name)
    EditText edtFirstName;
    @BindView(R.id.edt_last_name)
    EditText edtLastName;
    @BindView(R.id.edt_email_id)
    EditText edtEmailId;
    @BindView(R.id.edt_alternative_email_id)
    EditText edtAlternativeEmailId;
    @BindView(R.id.edt_employee_id)
    EditText edtEmployeeId;
    @BindView(R.id.edt_mobile_no)
    EditText edtMobileNo;
    @BindView(R.id.rd_grp_gender)
    RadioGroup radioGroupGender;
    @BindView(R.id.rd_male)
    RadioButton rdMale;
    @BindView(R.id.rd_female)
    RadioButton rdFemale;
    @BindView(R.id.btn_update_profile)
    Button btnUpdateProfile;
    private String gender;
    private String userId;
    private User user;
    private ChoosePhoto choosePhoto = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);
        activity.setUpEditProfileView();
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.rd_male) {
                    gender = "m";
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rd_female) {
                    gender = "f";
                }
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("user")) {
                user = bundle.getParcelable("user");
                if (user != null) {
                    setValues(user);
                }
            } else {
                getProfileInfo();
            }
        }
        return view;
    }

    private void getProfileInfo() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            uiUtils.showProgressDialog();
            UserStore.getInstance().getProfileInfo(sharedPreferenceUtils.getAccessToken(), new UserCallback() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        try {
                            uiUtils.dismissDialog();
                            setValues(user);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }

                @Override
                public void onFailure(Error error) {
                    uiUtils.dismissDialog();
                    uiUtils.shortToast("Unable to fetch profile details!");
                }
            });
        } else {
            uiUtils.shortToast("No Internet!");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (activity != null) activity.setTitle(getString(R.string.update_profile));
    }

    @OnClick(R.id.btn_update_profile)
    public void onSave() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            String firstName = edtFirstName.getText().toString();
            String lastName = edtLastName.getText().toString();
            String email = edtEmailId.getText().toString();
            String alternateEmail = edtAlternativeEmailId.getText().toString();
            String phone = edtMobileNo.getText().toString();
            String employeeId = edtEmployeeId.getText().toString();
            if (validate(email, firstName, lastName, gender, phone, alternateEmail, employeeId)) {
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setAlternateEmail(alternateEmail);
                user.setPhoneNumber("+91" + phone);
                user.setGender(gender);
                user.setId(userId);
                user.setEmployeeId(employeeId);
                updateProfile(user);
            }
        } else {
            uiUtils.shortToast("No Internet!");
        }
    }


    private void setValues(User user) {
        this.user = user;
        edtFirstName.setText(Utils.replaceNull(user.getFirstName()));
        edtLastName.setText(Utils.replaceNull(user.getLastName()));
        edtEmailId.setText(Utils.replaceNull(user.getEmail()));
        edtAlternativeEmailId.setText(Utils.replaceNull(user.getAlternateEmail()));
        edtEmployeeId.setText(Utils.replaceNull(user.getEmployeeId()));
        edtMobileNo.setText(Utils.replaceNull(user.getPhoneNumber()).replace("+91", ""));
        userId = Utils.replaceNull(user.getId());
        String gender = Utils.replaceNull(user.getGender());
        if (gender.equalsIgnoreCase("m")) {
            rdMale.setChecked(true);
        } else if (gender.equalsIgnoreCase("f")) {
            rdFemale.setChecked(true);
        }
        setProfilePic();
    }

    private void setProfilePic() {
        if (user.getProfilePicUri() != null) {
            if (!user.getProfilePicUri().equalsIgnoreCase("")) {
                String encodedImage = user.getProfilePicUri();
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgProfile.setImageBitmap(decodedByte);
            }
        }
    }

    private void updateProfile(User user) {
        uiUtils.showProgressDialog();
        user.setId(userId);
        UserStore.getInstance().updateProfileInfo(sharedPreferenceUtils.getAccessToken(), user, new UserCallback() {
            @Override
            public void onSuccess(User user) {
                uiUtils.dismissDialog();
                sharedPreferenceUtils.saveImageData(getImageUri());
                checkProfileStatus();
            }

            @Override
            public void onFailure(Error error) {
                uiUtils.dismissDialog();
                uiUtils.shortToast("Unable to update profile!");
            }
        });
    }

    private void checkProfileStatus() {
        if (user.getShift() == null) {
            activity.backAllowed = false;
            GroupAndShiftFragment groupAndShiftFragment = new GroupAndShiftFragment();
            activity.replaceFragment(groupAndShiftFragment, getString(R.string.group_and_shift));
        } else {
            activity.onBackPressed();
        }
    }

    private String getImageUri() {
        Bitmap image = ((BitmapDrawable) imgProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        return Base64.encodeToString(imageInByte, Base64.DEFAULT);
    }

    @OnClick(R.id.img_profile)
    void onClickImage() {
        if ((ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } else {
            choosePhoto = new ChoosePhoto(activity);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            choosePhoto = new ChoosePhoto(activity);
        } else {
            uiUtils.shortToast("Camera permission is required");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ChoosePhoto.CHOOSE_PHOTO_INTENT) {
                uiUtils.showProgressDialog();
                if (data != null && data.getData() != null) {
                    choosePhoto.handleGalleryResult(data);
                } else {
                    choosePhoto.handleCameraResult(choosePhoto.getCameraUri());
                }
            } else if (requestCode == ChoosePhoto.SELECTED_IMG_CROP) {
                uiUtils.dismissDialog();
                imgProfile.setImageURI(choosePhoto.getCropImageUrl());
                user.setProfilePicUri(getImageUri());
            }
        }

    }

    private boolean validate(String email, String firstName, String lastName, String gender, String phoneNo, String alternateEmail, String employeeId) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiUtils.shortToast("Enter Valid Email");
            edtEmailId.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(firstName)) {
            uiUtils.shortToast("Enter First Name");
            edtFirstName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            uiUtils.shortToast("Enter Last Name");
            edtLastName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phoneNo)) {
            uiUtils.shortToast("Enter Phone Number");
            edtMobileNo.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            uiUtils.shortToast("Please Select Gender");
            radioGroupGender.requestFocus();
            return false;
        }

        if (phoneNo.length() != 10) {
            uiUtils.shortToast("Please provide a 10 digit mobile number, without adding any prefix!");
            edtMobileNo.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(employeeId)) {
            uiUtils.shortToast("Please Enter Employee Id");
            radioGroupGender.requestFocus();
            return false;
        }
        return true;
    }
}
