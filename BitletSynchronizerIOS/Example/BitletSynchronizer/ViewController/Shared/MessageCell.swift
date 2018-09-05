//
//  MessageCell.swift
//  Bitlet Synchronizer example
//
//  A cell for a message item in the table view controllers, for example, to show that there is no data
//

import UIKit

class MessageCell: UITableViewCell {

    // --
    // MARK: Identifier
    // --

    static let cellIdentifier = "messageCell"

    
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
