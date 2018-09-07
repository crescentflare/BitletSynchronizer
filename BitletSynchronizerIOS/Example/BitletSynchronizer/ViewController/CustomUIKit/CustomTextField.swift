//
//  CustomTextField.swift
//  Bitlet Synchronizer example
//
//  Custom UIKit: extends text field to allow setting the next text field with interface builder
//

import UIKit

class CustomTextField: UITextField, UITextFieldDelegate {

    // --
    // MARK: IB Outlets
    // --
    
    @objc @IBOutlet weak var nextResponderView: UIResponder?

    
    // --
    // MARK: Members
    // --
    
    private weak var _delegate: UITextFieldDelegate?
    
    override var delegate: UITextFieldDelegate? {
        set {
            _delegate = newValue
        }
        get { return _delegate }
    }


    // --
    // MARK: Initialization
    // --
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setup()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }
    
    func setup() {
        super.delegate = self
    }
    

    // --
    // MARK: UITextFieldDelegate
    // --
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        nextResponderView?.becomeFirstResponder()
        _ = _delegate?.textFieldShouldReturn?(textField)
        return false
    }
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        return _delegate?.textFieldShouldBeginEditing?(textField) ?? true
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        _delegate?.textFieldDidBeginEditing?(textField)
    }
    
    func textFieldShouldEndEditing(_ textField: UITextField) -> Bool {
        return _delegate?.textFieldShouldEndEditing?(textField) ?? true
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        _delegate?.textFieldDidEndEditing?(textField)
    }
    
    func textFieldShouldClear(_ textField: UITextField) -> Bool {
        return _delegate?.textFieldShouldClear?(textField) ?? true
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        return _delegate?.textField?(textField, shouldChangeCharactersIn: range, replacementString: string) ?? true
    }
    
}
