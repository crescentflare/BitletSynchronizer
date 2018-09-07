//
//  ErrorCell.swift
//  Bitlet Synchronizer example
//
//  A cell for an error message item in the table view controllers
//

import UIKit

class ErrorCell: UITableViewCell {

    // --
    // MARK: Identifier
    // --

    static let cellIdentifier = "errorCell"

    
    // --
    // MARK: IB Outlets
    // --
    
    @objc @IBOutlet weak var labelView: UILabel?


    // --
    // MARK: Members
    // --
    
    var label: String? {
        set {
            labelView?.text = newValue
        }
        get { return labelView?.text }
    }
    
}
