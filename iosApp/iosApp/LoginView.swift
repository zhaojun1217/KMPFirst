import SwiftUI

struct LoginView: View {
    @StateObject private var viewModel = LoginViewModelWrapper()
    @State private var username = ""
    @State private var password = ""

    var body: some View {
        Group {
            if viewModel.state.isChecking {
                ProgressView()
            } else if viewModel.state.isLoggedIn {
                loggedInContent
            } else {
                loginForm
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }

    private var loginForm: some View {
        VStack(spacing: 16) {
            Text("登录")
                .font(.largeTitle)
                .fontWeight(.semibold)

            TextField("用户名", text: $username)
                .textFieldStyle(.roundedBorder)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .disabled(viewModel.state.isLoggingIn)
                .onChange(of: username) { _, _ in
                    if viewModel.state.errorMessage != nil {
                        viewModel.clearError()
                    }
                }

            SecureField("密码", text: $password)
                .textFieldStyle(.roundedBorder)
                .disabled(viewModel.state.isLoggingIn)
                .onChange(of: password) { _, _ in
                    if viewModel.state.errorMessage != nil {
                        viewModel.clearError()
                    }
                }

            if let errorMessage = viewModel.state.errorMessage {
                Text(errorMessage)
                    .font(.footnote)
                    .foregroundStyle(.red)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }

            Button(action: { viewModel.login(username: username, password: password) }) {
                Group {
                    if viewModel.state.isLoggingIn {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                    } else {
                        Text("登录")
                            .frame(maxWidth: .infinity)
                    }
                }
            }
            .buttonStyle(.borderedProminent)
            .disabled(viewModel.state.isLoggingIn)

            Text("演示账号：demo / 123456")
                .font(.footnote)
                .foregroundStyle(.secondary)
        }
        .padding(24)
    }

    private var loggedInContent: some View {
        VStack(spacing: 12) {
            Text("欢迎，\(viewModel.state.displayName ?? "")")
                .font(.title2)
                .fontWeight(.semibold)

            if let username = viewModel.state.username {
                Text("@\(username)")
                    .font(.body)
                    .foregroundStyle(.secondary)
            }

            Button("退出登录", role: .destructive) {
                viewModel.logout()
            }
            .buttonStyle(.bordered)
            .padding(.top, 16)
        }
        .padding(24)
    }
}
