//
//  OverviewCell.swift
//  Bitlet Synchronizer example
//
//  A cell for a usage overview item in the overview view controller
//

import UIKit

class OverviewCell: UITableViewCell {

    // --
    // MARK: Identifier
    // --

    static let cellIdentifier = "overviewCell"

    
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
