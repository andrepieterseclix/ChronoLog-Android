package za.co.vitalsoft.chronolog.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import za.co.vitalsoft.chronolog.Identity;
import za.co.vitalsoft.chronolog.R;
import za.co.vitalsoft.chronolog.interfaces.BaseActivityContract;
import za.co.vitalsoft.chronolog.models.User;
import za.co.vitalsoft.chronolog.services.access.AccessService;
import za.co.vitalsoft.chronolog.services.access.models.UpdateUserRequest;
import za.co.vitalsoft.chronolog.services.access.models.UpdateUserResponse;

/**
 * Created by Andre on 2017-07-23.
 */

public class ModifyProfileFragment extends Fragment implements TextWatcher, Callback<UpdateUserResponse> {

    private BaseActivityContract mParent;
    private User mUser;
    private TextView mUsernameTextView;
    private EditText mNameEditText;
    private EditText mSurnameEditText;
    private EditText mEmailEditText;
    private Button mSaveButton;
    private Button mResetButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Identity identity = Identity.getInstance();
        mUser = identity.getUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.fragment_modify_profile, container, false);

        mUsernameTextView = view.findViewById(R.id.username_text_view);
        mNameEditText = view.findViewById(R.id.nameEditText);
        mSurnameEditText = view.findViewById(R.id.surnameEditText);
        mEmailEditText = view.findViewById(R.id.email_editText);
        mSaveButton = view.findViewById(R.id.save_button);
        mResetButton = view.findViewById(R.id.reset_button);

        mNameEditText.addTextChangedListener(this);
        mSurnameEditText.addTextChangedListener(this);
        mEmailEditText.addTextChangedListener(this);

        reset();

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParent = (BaseActivityContract) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mParent = null;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        boolean hasChanges =
                !mUser.getName().equals(mNameEditText.getText().toString()) ||
                        !mUser.getSurname().equals(mSurnameEditText.getText().toString()) ||
                        !mUser.getEmail().equals(mEmailEditText.getText().toString());

        mSaveButton.setEnabled(hasChanges);
        mResetButton.setEnabled(hasChanges);
    }

    private void reset() {
        mUsernameTextView.setText(mUser.getUserName());
        mNameEditText.setText(mUser.getName());
        mSurnameEditText.setText(mUser.getSurname());
        mEmailEditText.setText(mUser.getEmail());
    }

    private void save() {
        mParent.showBusyFragment(null);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName(mNameEditText.getText().toString());
        updateUserRequest.setSurname(mSurnameEditText.getText().toString());
        updateUserRequest.setEmail(mEmailEditText.getText().toString());

        Retrofit retrofit = mParent.buildRetrofit(true);
        AccessService accessService = retrofit.create(AccessService.class);
        Call<UpdateUserResponse> call = accessService.putUser(mUser.getUserName(), updateUserRequest);

        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<UpdateUserResponse> call, @NonNull Response<UpdateUserResponse> response) {
        mParent.finishBusyFragment();

        UpdateUserResponse updateUserResponse = mParent.interpretServiceResponse(response);

        if (updateUserResponse == null) {
            return;
        }

        // Update identity info
        Identity identity = Identity.getInstance();
        identity.setSessionId(updateUserResponse.getSession().getId());
        identity.setSessionKey(updateUserResponse.getSession().getSessionKey());

        mUser.setName(mNameEditText.getText().toString());
        mUser.setSurname(mSurnameEditText.getText().toString());
        mUser.setEmail(mEmailEditText.getText().toString());
        reset();

        Log.i("info", "User updated");
        Toast.makeText(getActivity(), R.string.info_service_user_updated, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(@NonNull Call<UpdateUserResponse> call, @NonNull Throwable t) {
        mParent.finishBusyFragment();

        Log.e("error", "Could not update the user.", t);
        Toast.makeText(getActivity(), R.string.error_service_update_user, Toast.LENGTH_LONG).show();
    }
}
