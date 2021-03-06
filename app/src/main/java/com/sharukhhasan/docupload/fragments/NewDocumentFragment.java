package com.sharukhhasan.docupload.fragments;

import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import com.sharukhhasan.docupload.R;
import com.sharukhhasan.docupload.activities.UploadActivity;
import com.sharukhhasan.docupload.models.Document;

/**
 * Created by Sharukh on 2/21/16.
 */
public class NewDocumentFragment extends Fragment {
    private ImageButton photoButton;
    private Button saveButton;
    private Button cancelButton;
    private TextView documentName;
    private ImageView documentPreview;
    private RadioGroup radioGroup;
    private String radioButtonChoice;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle SavedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_new_document, parent, false);

        // Initialize view components
        documentName = ((EditText) v.findViewById(R.id.document_name));

        radioGroup = (RadioGroup) v.findViewById(R.id.myRadioGroup);
        radioGroup.setClickable(false);

        photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
        photoButton.setClickable(false);

        saveButton = ((Button) v.findViewById(R.id.save_button));
        saveButton.setClickable(false);

        // Make radiobuttons clickable when text is inputted into document title
        documentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                radioGroup.setClickable(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                radioGroup.setClickable(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                radioGroup.setClickable(true);
            }
        });

        // Find checked document type choice
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                photoButton.setClickable(true);

                if(checkedId == R.id.choiceW2)
                {
                    radioButtonChoice = "W2";
                }
                else if(checkedId == R.id.choiceVoidedCheck)
                {
                    radioButtonChoice = "Voided Check";
                }
                else if(checkedId == R.id.choiceUtilityBill)
                {
                    radioButtonChoice = "Utility Bill";
                }
            }
        });

        // Begin camera onClick
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                saveButton.setClickable(true);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(documentName.getWindowToken(), 0);
                startCamera();
            }
        });

        // Add document attributes to document
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Document document = ((UploadActivity) getActivity()).getCurrentDocument();

                // When the user clicks "Save," upload the document to Parse
                // Add data to the document obj:
                document.setTitle(documentName.getText().toString());

                // Annotate document with the current user
                document.setAuthor(ParseUser.getCurrentUser().getUsername());

                // Set document type
                document.setDocumentType(radioButtonChoice);

                // If the user added a photo, that data will be
                // added in the CameraFragment

                // Save the document and return
                document.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e)
                    {
                        if(e == null)
                        {
                            mProgressDialog = new ProgressDialog(getActivity());
                            mProgressDialog.setTitle("DocUpload");
                            mProgressDialog.setMessage("Avant will review your submission within 24 hours");
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.show();
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }
                        else
                        {
                            Toast.makeText(getActivity().getApplicationContext(), "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }
        });

        // Initialize cancel button, and onClick cancel
        cancelButton = ((Button) v.findViewById(R.id.cancel_button));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        // Until the user has taken a photo, hide the preview
        documentPreview = (ImageView) v.findViewById(R.id.document_preview_image);
        documentPreview.setVisibility(View.INVISIBLE);

        return v;
    }

    // Start CameraFragment
    public void startCamera()
    {
        Fragment cameraFragment = new CameraFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("documentName", documentName.getText().toString());
        cameraFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, cameraFragment);
        transaction.addToBackStack("NewDocumentFragment");
        transaction.commit();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        String docURL = ((UploadActivity) getActivity()).getCurrentDocument().getPhotoURL();

        if(docURL != null)
        {
            Glide.with(getActivity()).load(docURL).into(documentPreview);
            documentPreview.setVisibility(View.VISIBLE);
        }
    }
}
