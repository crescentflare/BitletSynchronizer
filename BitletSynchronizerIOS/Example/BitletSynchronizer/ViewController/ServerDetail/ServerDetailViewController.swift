//
//  ServerDetailViewController.swift
//  Bitlet Synchronizer example
//
//  Shows a demo of an detail page of a server with information
//

import UIKit
import Toast_Swift
import BitletSynchronizer

private enum DetailItemRow: Int {
    
    case name = 0
    case description = 1
    case operatingSystem = 2
    case location = 3
    case dataTraffic = 4
    case serverLoad = 5
    case enabled = 6

}

class ServerDetailViewController: UITableViewController {

    // --
    // MARK: Constants
    // --
    
    static let segueIdentifier = "showServerDetail"


    // --
    // MARK: Members
    // --
    
    var serverId: String?

    
    // --
    // MARK: Lifecycle
    // --

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        loadData(forced: false)
    }

    
    // --
    // MARK: Data loading
    // --
    
    private func cachedServer() -> Server? {
        return BitletSynchronizer.shared.cachedBitlet(forKey: Server.bitlet(serverId: serverId ?? "").cacheKey) as? Server
    }

    private func loadData(forced: Bool) {
        BitletSynchronizer.shared.loadBitlet(Server.bitlet(serverId: serverId ?? ""), cacheKey: Server.bitlet(serverId: serverId ?? "").cacheKey, forced: forced, completion: { server, error in
            if let error = error {
                self.showErrorToast(error)
            }
            self.tableView.reloadData()
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

    
    // --
    // MARK: IB Actions
    // --
    
    @objc @IBAction func doneButtonPressed() {
        dismiss(animated: true, completion: nil)
    }
    
    @objc @IBAction func pulledToRefresh() {
        loadData(forced: true)
        if BitletSynchronizer.shared.cacheState(forKey: Server.bitlet(serverId: serverId ?? "").cacheKey) == .loading {
            self.tableView.reloadData()
        }
    }

    
    // --
    // MARK: UITableViewDataSource
    // --
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cachedServer() == nil ? 1 : 7
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if BitletSynchronizer.shared.cacheState(forKey: Server.bitlet(serverId: serverId ?? "").cacheKey) == .loading, let cell = tableView.dequeueReusableCell(withIdentifier: LoadingCell.cellIdentifier) as? LoadingCell {
            cell.label = NSLocalizedString("SERVER_DETAILS_LOADING", comment: "")
            return cell
        } else if cachedServer() == nil, let cell = tableView.dequeueReusableCell(withIdentifier: ErrorCell.cellIdentifier) as? ErrorCell {
            cell.selectionStyle = .none
            cell.label = NSLocalizedString("SERVER_DETAILS_ERROR", comment: "")
            return cell
        } else if let cell = tableView.dequeueReusableCell(withIdentifier: ServerDetailCell.cellIdentifier) as? ServerDetailCell {
            if let row = DetailItemRow(rawValue: indexPath.row) {
                switch row {
                case .name:
                    cell.label = NSLocalizedString("SERVER_DETAILS_NAME", comment: "")
                    cell.value = cachedServer()?.name ?? ""
                case .description:
                    cell.label = NSLocalizedString("SERVER_DETAILS_DESCRIPTION", comment: "")
                    cell.value = cachedServer()?.description ?? ""
                case .operatingSystem:
                    cell.label = NSLocalizedString("SERVER_DETAILS_OPERATING_SYSTEM", comment: "")
                    cell.value = (cachedServer()?.os ?? "") + " " + (cachedServer()?.osVersion ?? "")
                case .location:
                    cell.label = NSLocalizedString("SERVER_DETAILS_LOCATION", comment: "")
                    cell.value = cachedServer()?.location ?? ""
                case .dataTraffic:
                    cell.label = NSLocalizedString("SERVER_DETAILS_DATA_TRAFFIC", comment: "")
                    cell.value = cachedServer()?.dataTraffic?.label ?? ""
                case .serverLoad:
                    cell.label = NSLocalizedString("SERVER_DETAILS_SERVER_LOAD", comment: "")
                    cell.value = cachedServer()?.serverLoad?.label ?? ""
                case .enabled:
                    cell.label = NSLocalizedString("SERVER_DETAILS_ENABLED", comment: "")
                    cell.value = NSLocalizedString((cachedServer()?.enabled ?? false) ? "SERVER_DETAILS_ENABLED_ON" : "SERVER_DETAILS_ENABLED_OFF", comment: "")
                }
            }
            return cell
        }
        return UITableViewCell()
    }

}
