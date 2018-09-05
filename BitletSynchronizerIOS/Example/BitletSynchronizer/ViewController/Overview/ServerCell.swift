//
//  ServerCell.swift
//  Bitlet Synchronizer example
//
//  A cell for a virtual server item in the overview view controller
//

import UIKit

class ServerCell: UITableViewCell {

    // --
    // MARK: Identifier
    // --
    
    static let cellIdentifier = "serverCell"
    
    
    // --
    // MARK: IB Outlets
    // --
    
    @objc @IBOutlet weak var labelView: UILabel?
    @objc @IBOutlet weak var additionalView: UILabel?
    @objc @IBOutlet weak var valueView: UILabel?


    // --
    // MARK: Members
    // --
    
    var label: String? {
        set {
            labelView?.text = newValue
        }
        get { return labelView?.text }
    }
    
    var additional: String? {
        set {
            additionalView?.text = newValue
        }
        get { return additionalView?.text }
    }
    
    var value: String? {
        set {
            valueView?.text = newValue
        }
        get { return valueView?.text }
    }

}
