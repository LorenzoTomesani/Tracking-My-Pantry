package it.unibo.lorenzo;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import it.unibo.lorenzo.ApiManager;
public class LoginFragment extends Fragment {

    private ApiManager apiManager;
    private Button login;
    private String email = "";
    private String accessToken="";
    private boolean alreadyPressed = false;
    public LoginFragment() { }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void  onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        apiManager = new ApiManager(getActivity().getApplicationContext());
        login = getView().findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!alreadyPressed) {
                    alreadyPressed = true;
                    EditText editTextEmail = getView().findViewById(R.id.editTextEmail);
                    email = editTextEmail.getText().toString();


                    EditText editTextPassword = getView().findViewById(R.id.editTextPassword);
                    String psw = editTextPassword.getText().toString();
                    apiManager.login(email, psw, new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            alreadyPressed = false;
                            try {
                                accessToken = result.getString("accessToken");
                            } catch (Exception e) {
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error), duration);
                                toast.show();
                            }
                            goToPantry(email, accessToken);
                        }

                        @Override
                        public void onError() {
                            alreadyPressed = false;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.login_fail), duration);
                            toast.show();
                        }
                    });
                }
            }
        });
    }

    public void goToPantry(String email, String accessToken){
        Intent myIntent = new Intent(getActivity().getApplicationContext(), Pantry.class);
        myIntent.putExtra("accessToken",accessToken);
        myIntent.putExtra("email",email);
        startActivity(myIntent);

    }
}