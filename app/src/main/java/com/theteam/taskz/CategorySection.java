package com.theteam.taskz;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theteam.taskz.data.AuthenticationDataHolder;
import com.theteam.taskz.data.ViewPagerDataHolder;
import com.theteam.taskz.enums.AccountType;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.SelectableButton;
import com.theteam.taskz.view_models.UnderlineTextView;

public class CategorySection extends Fragment {

    private SelectableButton businessAccount,familyAccount,personalAccount;
    private AccountType selectedAccount;

    private LoadableButton button;
    private UnderlineTextView back;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        businessAccount = (SelectableButton) view.findViewById(R.id.business_account);
        familyAccount = (SelectableButton) view.findViewById(R.id.family_account);
        personalAccount = (SelectableButton) view.findViewById(R.id.personal_account);

        button = view.findViewById(R.id.loadable_button);
        back = view.findViewById(R.id.back);

        back.setOnClickListener(view1 -> {
            ViewPagerDataHolder.viewPager.setCurrentItem(0, true);
        });

        businessAccount.setOnClickListener(view1 -> {
            businessAccount.select(true);
            personalAccount.select(false);
            familyAccount.select(false);

            selectedAccount = AccountType.Business;
        });
        familyAccount.setOnClickListener(view1 -> {
            familyAccount.select(true);
            personalAccount.select(false);
            businessAccount.select(false);

            selectedAccount = AccountType.Family;
        });
        personalAccount.setOnClickListener(view1 -> {
            personalAccount.select(true);
            businessAccount.select(false);
            familyAccount.select(false);

            selectedAccount = AccountType.Personal;
        });

        button.setOnClickListener(view1 -> {
            AuthenticationDataHolder.selecAccountType = selectedAccount;
            button.startLoading();
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            button.stopLoading();
                            ViewPagerDataHolder.viewPager.setCurrentItem(2, true);
                        }
                    },
                    2500
            );
        });
    }
}
