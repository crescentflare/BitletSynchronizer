//
//  ServerDetailCellItem.swift
//  Bitlet Synchronizer example
//
//  A view model for the server detail view cells
//

import UIKit

enum ServerDetailCellItemType {
    
    case loading
    case error
    case info
    
}

class ServerDetailCellItem {

    // --
    // MARK: Members
    // --

    let type: ServerDetailCellItemType
    let label: String
    let value: String?


    // --
    // MARK: Initialization
    // --
    
    init(withLoadingText: String) {
        type = .loading
        label = withLoadingText
        value = nil
    }

    init(withErrorText: String) {
        type = .error
        label = withErrorText
        value = nil
    }
    
    init(withLabel: String, andValue: String) {
        type = .info
        label = withLabel
        value = andValue
    }

}
