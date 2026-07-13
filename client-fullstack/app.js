const SSO_FRONTEND_URL = 'http://localhost:5173/oauth/authorize';
const SSO_BACKEND_URL = 'http://localhost:8080';

const loginView = document.querySelector('#login-view');
const callbackView = document.querySelector('#callback-view');
const showFormButton = document.querySelector('#show-form-button');
const authorizationForm = document.querySelector('#authorization-form');
const exchangeForm = document.querySelector('#exchange-form');
const clientIdInput = document.querySelector('#client-id');
const redirectUriInput = document.querySelector('#redirect-uri');
const clientSecretInput = document.querySelector('#client-secret');
const authorizationCodeElement = document.querySelector('#authorization-code');
const statusMessage = document.querySelector('#status-message');
const resultElement = document.querySelector('#result');

const callbackUri = `${window.location.origin}/auth/callback`;
redirectUriInput.value = callbackUri;

showFormButton.addEventListener('click', () => {
  showFormButton.classList.add('hidden');
  authorizationForm.classList.remove('hidden');
  clientIdInput.focus();
});

authorizationForm.addEventListener('submit', (event) => {
  event.preventDefault();

  const clientId = clientIdInput.value.trim();
  const redirectUri = redirectUriInput.value.trim();

  sessionStorage.setItem('oauth.clientId', clientId);
  sessionStorage.setItem('oauth.redirectUri', redirectUri);

  const authorizationUrl = new URL(SSO_FRONTEND_URL);
  authorizationUrl.searchParams.set('client_id', clientId);
  authorizationUrl.searchParams.set('redirect_uri', redirectUri);
  window.location.assign(authorizationUrl);
});

exchangeForm.addEventListener('submit', async (event) => {
  event.preventDefault();

  const code = new URLSearchParams(window.location.search).get('code');
  const clientId = sessionStorage.getItem('oauth.clientId');
  const redirectUri = sessionStorage.getItem('oauth.redirectUri');
  const clientSecret = clientSecretInput.value.trim();
  const submitButton = exchangeForm.querySelector('button[type="submit"]');

  if (!code || !clientId || !redirectUri) {
    showStatus('Thiếu code, clientId hoặc redirectUri. Hãy bắt đầu lại flow đăng nhập.', true);
    return;
  }

  submitButton.disabled = true;
  showStatus('Đang đổi authorization code...', false);
  resultElement.classList.add('hidden');

  try {
    const response = await fetch(`${SSO_BACKEND_URL}/oauth/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        code,
        clientId,
        clientSecret,
        redirectUri,
      }),
    });
    const responseBody = await response.json();

    console.log('Token exchange response:', responseBody);
    resultElement.textContent = JSON.stringify(responseBody, null, 2);
    resultElement.classList.remove('hidden');

    if (!response.ok) {
      throw new Error(responseBody.error?.message || 'Không thể đổi authorization code');
    }

    showStatus('Đổi code thành công. Thông tin user được hiển thị bên dưới.', false);
  } catch (error) {
    console.error('Token exchange failed:', error);
    showStatus(error.message || 'Không thể kết nối đến SSO Server.', true);
  } finally {
    submitButton.disabled = false;
  }
});

function showCallbackView() {
  const code = new URLSearchParams(window.location.search).get('code');

  if (window.location.pathname !== '/auth/callback') {
    return;
  }

  loginView.classList.add('hidden');
  callbackView.classList.remove('hidden');

  if (!code) {
    showStatus('Callback không chứa authorization code.', true);
    exchangeForm.classList.add('hidden');
    return;
  }

  authorizationCodeElement.textContent = code;
  clientSecretInput.focus();
}

function showStatus(message, isError) {
  statusMessage.textContent = message;
  statusMessage.classList.toggle('error', isError);
  statusMessage.classList.remove('hidden');
}

showCallbackView();
