//
//  LoadingCell.swift
//  Bitlet Synchronizer example
//
//  A cell for a loading item in the table view controllers
//

import UIKit

class LoadingCell: UITableViewCell {

    // --
    // MARK: Identifier
    // --

    static let cellIdentifier = "loadingCell"

    
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
