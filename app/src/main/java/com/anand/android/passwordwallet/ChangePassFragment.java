package com.anand.android.passwordwallet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class ChangePassFragment extends Fragment {
    private String userEmail;
    DbHelper dbHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DbHelper(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        userEmail = getArguments().getString("email");
        return inflater.inflate(R.layout.activity_changepass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button changePass = requireActivity().findViewById(R.id.proceedToPassChange);
        final EditText CurrentPass = requireActivity().findViewById(R.id.currentPassword);
        final EditText NewPass = requireActivity().findViewById(R.id.newPassword);
        final EditText ConfirmPass = requireActivity().findViewById(R.id.confirmPassword);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NewPass.getText().toString().trim().equals(ConfirmPass.getText().toString().trim())) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog.setMessage("Recheck the entered passwords...");
                    dialog.setTitle("Confirmation Error");
                    dialog.setIcon(R.drawable.ic_block);
                    dialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    CurrentPass.setText("");
                                    NewPass.setText("");
                                    ConfirmPass.setText("");
                                }
                            });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                } else {
                    if (!dbHelper.checked(userEmail, CurrentPass.getText().toString().trim())) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                        dialog.setMessage("Wrong Password...");
                        dialog.setTitle("Error ");
                        dialog.setIcon(android.R.drawable.ic_dialog_alert);
                        dialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        CurrentPass.setText("");
                                        NewPass.setText("");
                                        ConfirmPass.setText("");
                                    }
                                });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    } else {
                        boolean update = dbHelper.update(userEmail, NewPass.getText().toString().trim());
                        if (update) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                            dialog.setMessage("Login with your latest password... ");
                            dialog.setTitle("Exiting...");
                            dialog.setIcon(R.drawable.ic_logout);
                            dialog.setPositiveButton("EXIT",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            getActivity().finish();
                                        }
                                    });
                            AlertDialog alertDialog = dialog.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        }
                    }
                }
            }
        });

    }

}
