Feature('Authentication API');

const userData = {
    fullName: 'Nguyen Van Test',
    emailAddress: 'testuser@example.com',
    phoneNumber: '0123456777',
    password: 'string123'
};

let accessToken, refreshToken;

// ĐĂNG KÝ
Scenario('Register - happy path', async ({ I }) => {
    const res = await I.sendPostRequest('/api/auth/register', userData);
    I.seeResponseCodeIsSuccessful();
    I.seeResponseContainsJson({ message: 'Registration successful.' });
});

// ĐĂNG KÝ BỊ TRÙNG EMAIL
Scenario('Register - email already exists', async ({ I }) => {
  const res = await I.sendPostRequest('/api/auth/register', userData);
  I.seeResponseCodeIs(409);
  I.seeResponseContainsJson({ code: "ALREADY_EXISTS" });
});

// ĐĂNG KÝ THIẾU TRƯỜNG
Scenario('Register - missing field', async ({ I }) => {
  const invalid = { ...userData };
  delete invalid.fullName;
  const res = await I.sendPostRequest('/api/auth/register', invalid);
  I.seeResponseCodeIs(400);
  I.seeResponseContainsKeys(['message']);
});

// ĐĂNG NHẬP
Scenario('Login - happy path', async ({ I }) => {
  const res = await I.sendPostRequest('/api/auth/login', {
    userName: 'user@example.com',
    password: 'string'
  });
  I.seeResponseCodeIsSuccessful();
  I.seeResponseContainsKeys(['requiresVerification']);
  if (res.data.requiresVerification) {
    I.dontSeeInResponse('accessToken');
    I.dontSeeInResponse('refreshToken');
  } else {
    I.seeResponseContainsKeys(['accessToken', 'refreshToken', 'expiresIn', 'user']);
    I.seeResponseContainsJson({ user: { email: 'user@example.com' } });
    refreshToken = res.data.refreshToken;
  }
});

// ĐĂNG NHẬP SAI PASSWORD
Scenario('Login - wrong password', async ({ I }) => {
  const res = await I.sendPostRequest('/api/auth/login', {
    userName: userData.emailAddress,
    password: 'WrongPassword'
  });
  I.seeResponseCodeIs(401);
  I.seeResponseContainsJson({ code: "INVALID_CREDENTIALS" });
  I.seeResponseContainsJson({ message: "Invalid username or password" });
});

// ĐĂNG NHẬP USER KHÔNG TỒN TẠI
Scenario('Login - user not exist', async ({ I }) => {
  const res = await I.sendPostRequest('/api/auth/login', {
    userName: 'nouser@example.com',
    password: 'Whatever2025'
  });
  I.seeResponseCodeIs(404);
  I.seeResponseContainsJson({ code: "NOT_FOUND" });
  I.seeResponseContainsJson({ message: "User not found: nouser@example.com" });
});

// REFRESH TOKEN (nếu đăng nhập thành công và có token)
/// ... tương tự logic kiểm tra accessToken, refreshToken, user

// LOGOUT
Scenario('Logout - happy path', async ({ I }) => {
    if (!refreshToken) {
        throw new Error('No refreshToken available for logout test!');
    }
  const res = await I.sendPostRequest('/api/auth/logout', { refreshToken });
  I.seeResponseCodeIsSuccessful();
  I.seeResponseContainsJson({ message: "Logout successful" });
  // Thử refresh lại (token bị revoke)
  const res2 = await I.sendPostRequest('/api/auth/refresh', { refreshToken });
  I.seeResponseCodeIs(401);
  I.seeResponseContainsJson({ code: "TOKEN_EXPIRED" });
});