//
//  OverviewCellItem.swift
//  Bitlet Synchronizer example
//
//  A view model for the overview view cells
//

import UIKit

enum OverviewCellItemType {
    
    case loading
    case error
    case usage
    case server
    
}

class OverviewCellItem {

    // --
    // MARK: Members
    // --

    let type: OverviewCellItemType
    let label: String
    let value: String?
    let additional: String?


    // --
    // MARK: Initialization
    // --
    
    init(withLoadingText: String) {
        type = .loading
        label = withLoadingText
        value = nil
        additional = nil
    }

    init(withErrorText: String) {
        type = .error
        label = withErrorText
        value = nil
        additional = nil
    }
    
    init(withUsageLabel: String, andValue: String) {
        type = .usage
        label = withUsageLabel
        value = andValue
        additional = nil
    }

    init(withServerName: String, andLocation: String, enabled: Bool) {
        type = .server
        label = withServerName
        value = NSLocalizedString(enabled ? "OVERVIEW_SERVER_ENABLED" : "OVERVIEW_SERVER_DISABLED", comment: "")
        additional = andLocation
    }

}
