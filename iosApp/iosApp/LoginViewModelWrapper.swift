import Foundation
import Shared

@MainActor
final class LoginViewModelWrapper: ObservableObject {
    @Published private(set) var state = LoginUiStateSnapshot(
        isChecking: true,
        isLoggedIn: false,
        isLoggingIn: false,
        errorMessage: nil,
        displayName: nil,
        username: nil
    )

    private let bridge = LoginBridge()

    init() {
        bridge.startObserving { [weak self] snapshot in
            Task { @MainActor in
                self?.state = snapshot
            }
        }
    }

    deinit {
        bridge.close()
    }

    func login(username: String, password: String) {
        bridge.login(username: username, password: password)
    }

    func logout() {
        bridge.logout()
    }

    func clearError() {
        bridge.clearError()
    }
}
