package it.unibo.lorenzo;

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


public class RegisterFragment extends Fragment {

    private ApiManager apiManager;
    private Button register;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void  onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        apiManager = new ApiManager(getActivity().getApplicationContext());
        register = getView().findViewById(R.id.buttonRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextEmail = getView().findViewById(R.id.editTextEmail);
                String email =editTextEmail.getText().toString();


                EditText editTextPassword = getView().findViewById(R.id.editTextPassword);
                String psw = editTextPassword.getText().toString();

                EditText editTextnick = getView().findViewById(R.id.editTextNickname);
                String nick = editTextnick.getText().toString();

                apiManager.register(email, psw, nick, new VolleyCallback(){
                    @Override
                    public void onSuccess(JSONObject result){
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText( getActivity().getApplicationContext(), "registrazione avvenuta correttamente!", duration);
                        toast.show();
                        editTextPassword.setText("");
                        editTextEmail.setText("");
                        editTextnick.setText("");
                    }
                    @Override
                    public void onError() {

                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText( getActivity().getApplicationContext(), "qualcosa é andato storto", duration);
                        toast.show();
                    }
                });
            }
        });
    }
}