import ClinicUI
import SwiftUI
import UIKit

@main
struct ClinicApp: App {
    var body: some Scene {
        WindowGroup {
            ClinicComposeView()
                .ignoresSafeArea()
        }
    }
}

private struct ClinicComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let apiBaseUrl = Bundle.main.object(forInfoDictionaryKey: "ClinicAPIBaseURL") as? String
        return MainViewControllerKt.MainViewController(apiBaseUrl: apiBaseUrl)
    }

    func updateUIViewController(_ controller: UIViewController, context: Context) {}
}
