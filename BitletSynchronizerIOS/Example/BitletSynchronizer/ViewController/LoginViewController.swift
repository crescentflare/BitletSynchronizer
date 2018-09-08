//
//  LoginViewController.swift
//  Bitlet Synchronizer example
//
//  Shows a demo of a user authentication screen with a field to configure the server address
//

import UIKit
import Toast_Swift
import BitletSynchronizer
import Alamofire

class LoginViewController: UIViewController, UITextFieldDelegate {

    // --
    // MARK: IB Outlets
    // --

    @objc @IBOutlet weak var loginButton: UIBarButtonItem?
    @objc @IBOutlet weak var mockServerField: UITextField?
    @objc @IBOutlet weak var usernameField: UITextField?
    @objc @IBOutlet weak var passwordField: UITextField?
    @objc @IBOutlet weak var activityIndicator: UIActivityIndicatorView?


    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
        loginButton?.isEnabled = false
        mockServerField?.text = Settings.serverAddress
        usernameField?.text = Settings.lastLoggedInUser
        passwordField?.delegate = self
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        enableView(true)
        passwordField?.text = nil
        loginButton?.isEnabled = false
    }

    
    // --
    // MARK: Helper
    // --
    
    func enableView(_ enabled: Bool) {
        activityIndicator?.isHidden = enabled
        loginButton?.isEnabled = enabled
        mockServerField?.isEnabled = enabled
        usernameField?.isEnabled = enabled
        passwordField?.isEnabled = enabled
    }
    

    // --
    // MARK: UITextFieldDelegate
    // --
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        passwordField?.resignFirstResponder()
        if loginButton?.isEnabled ?? false {
            continueButtonPressed()
        }
        return false
    }
    

    // --
    // MARK: IB Actions
    // --
    
    @objc @IBAction func continueButtonPressed() {
        Settings.serverAddress = mockServerField?.text
        enableView(false)
        BitletSynchronizer.shared.loadBitlet(Session.bitlet(username: self.usernameField?.text ?? "", password: self.passwordField?.text ?? ""), completion: { session, error in
            if let error = error {
                ToastManager.shared.position = .top
                ToastManager.shared.duration = 4
                if let toast = try? self.view.toastViewForMessage(error.localizedDescription, title: NSLocalizedString("ERROR_GENERIC_TITLE", comment: ""), image: nil, style: ToastManager.shared.style) {
                    UIApplication.shared.keyWindow?.showToast(toast)
                }
                self.enableView(true)
            } else if let session = session {
                if let serverHost = URL(string: Settings.serverAddress ?? "")?.host, let cookie = session.cookie {
                    let cookieProps: [HTTPCookiePropertyKey: Any] = [
                        HTTPCookiePropertyKey.domain: serverHost,
                        HTTPCookiePropertyKey.path: "/",
                        HTTPCookiePropertyKey.name: "SESSION_COOKIE",
                        HTTPCookiePropertyKey.value: cookie,
                        HTTPCookiePropertyKey.secure: "TRUE",
                        HTTPCookiePropertyKey.expires: NSDate(timeIntervalSinceNow: 300)
                    ]
                    if let httpCookie = HTTPCookie(properties: cookieProps) {
                        Settings.sessionCookie = cookie
                        Alamofire.HTTPCookieStorage.shared.setCookie(httpCookie)
                    }
                }
                Settings.lastLoggedInUser = self.usernameField?.text
                self.performSegue(withIdentifier: OverviewViewController.segueIdentifier, sender: self)
            }
        })
    }
    
    @objc @IBAction func moreInfoButtonPressed() {
        let alert = UIAlertController(title: NSLocalizedString("LOGIN_INFORMATION_TITLE", comment: ""), message: NSLocalizedString("LOGIN_INFORMATION_TEXT", comment: ""), preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: NSLocalizedString("DIALOG_OK", comment: ""), style: .cancel, handler: nil))
        present(alert, animated: true)
    }
    
    @objc @IBAction func didChangeUsernameText() {
        loginButton?.isEnabled = (usernameField?.text?.count ?? 0) > 0 && (passwordField?.text?.count ?? 0) > 0
    }

    @objc @IBAction func didChangePasswordText() {
        loginButton?.isEnabled = (usernameField?.text?.count ?? 0) > 0 && (passwordField?.text?.count ?? 0) > 0
    }
    
}
