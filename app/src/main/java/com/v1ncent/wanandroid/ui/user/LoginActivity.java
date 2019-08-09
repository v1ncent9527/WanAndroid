package com.v1ncent.wanandroid.ui.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.r0adkll.slidr.Slidr;
import com.v1ncent.wanandroid.R;
import com.v1ncent.wanandroid.api.JsonCallback;
import com.v1ncent.wanandroid.api.Urls;
import com.v1ncent.wanandroid.base.AppActivity;
import com.v1ncent.wanandroid.db.LitePalUtils;
import com.v1ncent.wanandroid.ui.user.pojo.UserBean;
import com.v1ncent.wanandroid.utils.ActivityUtils;
import com.v1ncent.wanandroid.utils.BarUtils;
import com.v1ncent.wanandroid.utils.Constant;
import com.v1ncent.wanandroid.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.v1ncent.wanandroid.R.id.input_password;
import static com.v1ncent.wanandroid.R.id.input_user_name;

/**
 * Created by v1ncent on 2018/3/15.
 */

public class LoginActivity extends AppActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.tv_user_name)
    AutoCompleteTextView tvUserName;
    @BindView(input_user_name)
    TextInputLayout inputUserName;
    @BindView(R.id.tv_password)
    EditText tvPassword;
    @BindView(input_password)
    TextInputLayout inputPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_forgot_password)
    Button btnForgotPassword;
    @BindView(R.id.btn_forgot_register)
    Button btnForgotRegister;
    @BindView(R.id.email_login_form)
    LinearLayout emailLoginForm;
    @BindView(R.id.form_login)
    ScrollView formLogin;
    @BindView(R.id.pregress_bar)
    ProgressBar pregressBar;
    @BindView(R.id.register_tv_user_name)
    AutoCompleteTextView registerTvUserName;
    @BindView(R.id.register_input_user_name)
    TextInputLayout registerInputUserName;
    @BindView(R.id.register_tv_password)
    EditText registerTvPassword;
    @BindView(R.id.register_input_password)
    TextInputLayout registerInputPassword;
    @BindView(R.id.register_tv_password_again)
    EditText registerTvPasswordAgain;
    @BindView(R.id.register_input_password_again)
    TextInputLayout registerInputPasswordAgain;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.register_pregress_bar)
    ProgressBar registerPregressBar;
    @BindView(R.id.email_register_form)
    LinearLayout emailRegisterForm;

    private LoginActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    private void initView() {
        Slidr.attach(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_login);
        BarUtils.setStatusBarColor(this, ContextCompat.getColor(mContext, R.color.colorPrimaryDark), 24);
        BarUtils.addMarginTopEqualStatusBarHeight(rootLayout);

    }

    @OnClick({R.id.btn_login, R.id.btn_forgot_register, R.id.btn_register})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                // Reset errors.
                inputUserName.setError(null);
                inputPassword.setError(null);

                String userName = tvUserName.getText().toString();
                String password = tvPassword.getText().toString();
                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(userName)) {
                    inputUserName.setError(getString(R.string.error_no_name));
                    focusView = tvUserName;
                    cancel = true;
                } else if (TextUtils.isEmpty(password)) {
                    inputPassword.setError(getString(R.string.error_invalid_password));
                    focusView = tvPassword;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    hideInput(btnLogin);
                    btnLogin.setVisibility(View.GONE);
                    pregressBar.setVisibility(View.VISIBLE);
                    login(userName, password);
                }
                break;

            case R.id.btn_forgot_register:
                getSupportActionBar().setTitle(R.string.register);
                emailLoginForm.setVisibility(View.GONE);
                emailRegisterForm.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_register:
                // Reset errors.
                registerInputUserName.setError(null);
                registerInputPassword.setError(null);
                registerInputPasswordAgain.setError(null);

                String registerInputUserName = registerTvUserName.getText().toString().trim();
                String registerInputPassword = registerTvPassword.getText().toString().trim();
                String registerInputPasswordAgain = registerTvPasswordAgain.getText().toString().trim();

                boolean cancel_register = false;
                View focusView_register = null;

                if (TextUtils.isEmpty(registerInputUserName)) {
                    registerTvUserName.setError(getString(R.string.error_no_name));
                    focusView_register = registerTvUserName;
                    cancel_register = true;
                } else if (TextUtils.isEmpty(registerInputPassword)) {
                    registerTvPassword.setError(getString(R.string.error_invalid_password));
                    focusView_register = registerTvPassword;
                    cancel_register = true;
                } else if (TextUtils.isEmpty(registerInputPasswordAgain)) {
                    registerTvPasswordAgain.setError(getString(R.string.error_invalid_password));
                    focusView_register = registerTvPasswordAgain;
                    cancel_register = true;
                }

                if (cancel_register) {
                    focusView_register.requestFocus();
                } else {
                    hideInput(btnLogin);
                    btnRegister.setVisibility(View.GONE);
                    registerPregressBar.setVisibility(View.VISIBLE);
                    register(registerInputUserName, registerInputPassword, registerInputPasswordAgain);
                }
                break;
        }
    }

    /**
     * 登录
     *
     * @param userName
     * @param passWord
     */
    private void login(String userName, String passWord) {
        OkGo.<UserBean>post(Urls.LOGIN)
                .tag(this)
                .params("username", userName)
                .params("password", passWord)
                .execute(new JsonCallback<UserBean>() {
                    @Override
                    public void onSuccess(Response<UserBean> response) {
                        UserBean loginResultBean = response.body();
                        LogUtils.i("login-->", loginResultBean.getData() != null ? loginResultBean.toString() : "null");
                        if (loginResultBean.getErrorCode() == 0) {
                            showSuccess("登录成功");
                            Constant.ISLOGINED = true;
                            ActivityUtils.startActivity(UserInfoActivity.class);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            showError(loginResultBean.getErrorMsg());
                        }
                    }

                    @Override
                    public void onError(Response<UserBean> response) {
                        showError(response.getException().getMessage());
                    }

                    @Override
                    public void onFinish() {
                        btnLogin.setVisibility(View.VISIBLE);
                        pregressBar.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * 注册
     *
     * @param registerInputUserName
     * @param registerInputPassword
     * @param registerInputPasswordAgain
     */
    private void register(final String registerInputUserName, final String registerInputPassword, String registerInputPasswordAgain) {
        OkGo.<UserBean>post(Urls.REGISTER)
                .tag(this)
                .params("username", registerInputUserName)
                .params("password", registerInputPassword)
                .params("repassword", registerInputPasswordAgain)
                .execute(new JsonCallback<UserBean>() {
                    @Override
                    public void onSuccess(Response<UserBean> response) {
                        UserBean registerResultBean = response.body();
                        LogUtils.i("register-->", registerResultBean.toString());
                        if (registerResultBean.getErrorCode() == 0) {
                            showSuccess("注册成功");
                            Constant.USERANME = registerInputUserName;
                            Constant.PASSWORD = registerInputPassword;
                            LitePalUtils.getInstance().saveUserInfo(registerResultBean);
                            login(registerInputUserName, registerInputPassword);
                        } else {
                            showError(registerResultBean.getErrorMsg());
                        }
                    }

                    @Override
                    public void onError(Response<UserBean> response) {
                        showError(response.getException().getMessage());
                    }

                    @Override
                    public void onFinish() {
                        btnRegister.setVisibility(View.VISIBLE);
                        registerPregressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void hideInput(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
