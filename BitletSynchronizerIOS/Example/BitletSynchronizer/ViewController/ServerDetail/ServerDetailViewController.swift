//
//  ServerDetailViewController.swift
//  Bitlet Synchronizer example
//
//  Shows a demo of an detail page of a server with information
//

import UIKit
import Toast_Swift
import BitletSynchronizer

class ServerDetailViewController: UITableViewController {

    // --
    // MARK: Constants
    // --
    
    static let segueIdentifier = "showServerDetail"


    // --
    // MARK: Members
    // --
    
    var serverId: String?
    private var cellItems = [ServerDetailCellItem]()

    
    // --
    // MARK: Lifecycle
    // --

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        loadData(forced: false)
        refreshCellItems()
    }

    
    // --
    // MARK: Data loading
    // --
    
    private func loadData(forced: Bool) {
        BitletSynchronizer.shared.loadBitlet(Server.bitlet(serverId: serverId ?? ""), cacheKey: Server.bitlet(serverId: serverId ?? "").cacheKey, forced: forced, completion: { server, error in
            if let error = error {
                self.showErrorToast(error)
            }
            self.refreshCellItems()
            if forced {
                self.refreshControl?.endRefreshing()
            }
        })
    }
    
    private func showErrorToast(_ error: Error) {
        ToastManager.shared.position = .top
        ToastManager.shared.duration = 4
        if let toast = try? self.view.toastViewForMessage(error.localizedDescription, title: NSLocalizedString("ERROR_GENERIC_TITLE", comment: ""), image: nil, style: ToastManager.shared.style) {
            UIApplication.shared.keyWindow?.showToast(toast)
        }
    }
    
    private func refreshCellItems() {
        cellItems = []
        if let server = BitletSynchronizer.shared.cachedBitlet(forKey: Server.bitlet(serverId: serverId ?? "").cacheKey) as? Server {
            cellItems.append(ServerDetailCellItem(withLabel: NSLocalizedString("SERVER_DETAILS_NAME", comment: ""), andValue: server.name ?? ""))
            cellItems.append(ServerDetailCellItem(withLabel: NSLocalizedString("SERVER_DETAILS_DESCRIPTION", comment: ""), andValue: server.description ?? ""))
            cellItems.append(ServerDetailCellItem(withLabel: NSLocalizedString("SERVER_DETAILS_OPERATING_SYSTEM", comment: ""), andValue: (server.os ?? "") + " " + (server.osVersion ?? "")))
            cellItems.append(ServerDetailCellItem(withLabel: NSLocalizedString("SERVER_DETAILS_LOCATION", comment: ""), andValue: server.location ?? ""))
            cellItems.append(ServerDetailCellItem(withLabel: NSLocalizedString("SERVER_DETAILS_DATA_TRAFFIC", comment: ""), andValue: server.dataTraffic?.label ?? ""))
            cellItems.append(ServerDetailCellItem(withLabel: NSLocalizedString("SERVER_DETAILS_SERVER_LOAD", comment: ""), andValue: server.serverLoad?.label ?? ""))
            cellItems.append(ServerDetailCellItem(withLabel: NSLocalizedString("SERVER_DETAILS_ENABLED", comment: ""), andValue: NSLocalizedString((server.enabled ?? false) ? "SERVER_DETAILS_ENABLED_ON" : "SERVER_DETAILS_ENABLED_OFF", comment: "")))
        } else if BitletSynchronizer.shared.cacheState(forKey: Server.bitlet(serverId: serverId ?? "").cacheKey) == .loading {
            cellItems.append(ServerDetailCellItem(withLoadingText: NSLocalizedString("SERVER_DETAILS_LOADING", comment: "")))
        } else {
            cellItems.append(ServerDetailCellItem(withErrorText: NSLocalizedString("SERVER_DETAILS_ERROR", comment: "")))
        }
        self.tableView.reloadData()
    }

    
    // --
    // MARK: IB Actions
    // --
    
    @objc @IBAction func doneButtonPressed() {
        dismiss(animated: true, completion: nil)
    }
    
    @objc @IBAction func pulledToRefresh() {
        loadData(forced: true)
        if BitletSynchronizer.shared.cacheState(forKey: Server.bitlet(serverId: serverId ?? "").cacheKey) == .loading {
            refreshCellItems()
        }
    }

    
    // --
    // MARK: UITableViewDataSource
    // --
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cellItems.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellItem = cellItems[indexPath.row]
        if cellItem.type == .loading, let cell = tableView.dequeueReusableCell(withIdentifier: LoadingCell.cellIdentifier) as? LoadingCell {
            cell.label = cellItem.label
            return cell
        } else if cellItem.type == .error, let cell = tableView.dequeueReusableCell(withIdentifier: ErrorCell.cellIdentifier) as? ErrorCell {
            cell.selectionStyle = .none
            cell.label = cellItem.label
            return cell
        } else if cellItem.type == .info, let cell = tableView.dequeueReusableCell(withIdentifier: ServerDetailCell.cellIdentifier) as? ServerDetailCell {
            cell.label = cellItem.label
            cell.value = cellItem.value
            return cell
        }
        return UITableViewCell()
    }

}
