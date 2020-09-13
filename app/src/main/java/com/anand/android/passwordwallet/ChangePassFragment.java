package com.anand.android.passwordwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class ChangePassFragment extends Fragment {
    DbHelper dbHelper;
    private String userEmail;

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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_changepass, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button changePass = requireActivity().findViewById(R.id.proceedToPassChange);
        final EditText CurrentPass = requireActivity().findViewById(R.id.currentPassword);
        final EditText NewPass = requireActivity().findViewById(R.id.newPassword);
        final EditText ConfirmPass = requireActivity().findViewById(R.id.confirmPassword);

        changePass.setOnClickListener(view1 -> {
            if (!dbHelper.checked(userEmail, CurrentPass.getText().toString().trim())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                dialog.setMessage("Wrong Password...");
                dialog.setTitle("Error ");
                dialog.setIcon(android.R.drawable.ic_dialog_alert);
                dialog.setPositiveButton("OK",
                        (dialog1, which) -> {
                            CurrentPass.setText("");
                            NewPass.setText("");
                            ConfirmPass.setText("");
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            } else if (!NewPass.getText().toString().trim().equals(ConfirmPass.getText().toString().trim())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                dialog.setMessage("Passwords do not match...");
                dialog.setTitle("Confirmation Error");
                dialog.setIcon(R.drawable.ic_block);
                dialog.setPositiveButton("RETRY",
                        (dialog12, which) -> {
                            CurrentPass.setText("");
                            NewPass.setText("");
                            ConfirmPass.setText("");
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            } else {
                if (NewPass.getText().toString().trim().isEmpty() || NewPass.getText().toString().trim().length() < 6) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                    dialog.setMessage("6 minimum characters required in password...");
                    dialog.setTitle("Credentials Error");
                    dialog.setIcon(R.drawable.ic_block);
                    dialog.setPositiveButton("RETRY",
                            (dialog13, which) -> {
                                CurrentPass.setText("");
                                NewPass.setText("");
                                ConfirmPass.setText("");
                            });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                } else {
                    boolean update = dbHelper.update(userEmail, NewPass.getText().toString().trim());
                    if (update) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                        dialog.setMessage("Login with your latest password... ");
                        dialog.setTitle("Redirecting to Login Page");
                        dialog.setIcon(R.drawable.ic_logout);
                        dialog.setPositiveButton("EXIT",
                                (dialog14, which) -> {
                                    Intent intent = new Intent(requireContext(), UserLoginActivity.class);
                                    startActivity(intent);
                                    requireActivity().finish();
                                });
                        AlertDialog alertDialog = dialog.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }
                }
            }
        });
    }
}
