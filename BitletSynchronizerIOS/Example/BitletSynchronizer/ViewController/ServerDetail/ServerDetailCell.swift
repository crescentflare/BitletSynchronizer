//
//  ServerDetailCell.swift
//  Bitlet Synchronizer example
//
//  A cell for a server detail item in the server details view controller
//

import UIKit

class ServerDetailCell: UITableViewCell {

    // --
    // MARK: Identifier
    // --

    static let cellIdentifier = "serverDetailCell"

    
    // --
    // MARK: IB Outlets
    // --
    
    @objc @IBOutlet weak var labelView: UILabel?
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
    
    var value: String? {
        set {
            valueView?.text = newValue
        }
        get { return valueView?.text }
    }

}
