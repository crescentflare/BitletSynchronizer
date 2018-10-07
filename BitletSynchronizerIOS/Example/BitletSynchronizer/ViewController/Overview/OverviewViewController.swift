//
//  OverviewViewController.swift
//  Bitlet Synchronizer example
//
//  Shows a demo of an account usage and server list
//

import UIKit
import Toast_Swift
import Alamofire
import BitletSynchronizer

private enum OverviewSection: Int {
    
    case usage = 0
    case server = 1
    
}

class OverviewViewController: UITableViewController {

    // --
    // MARK: Constants
    // --
    
    static let segueIdentifier = "showOverview"


    // --
    // MARK: Members
    // --
    
    private var lastSelectedServerId: String?
    private var usageCellItems = [OverviewCellItem]()
    private var serverCellItems = [OverviewCellItem]()

    
    // --
    // MARK: Lifecycle
    // --

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        loadData(forced: false)
        refreshCellItems()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if isMovingFromParent {
            if let serverAddress = Settings.serverAddress {
                Alamofire.request(serverAddress + "/sessions/" + (Settings.sessionCookie ?? ""), method: .delete).response { response in
                    // No implementation
                }
            }
            BitletSynchronizer.shared.clearCache()
            Alamofire.HTTPCookieStorage.shared.removeCookies(since: Date(timeIntervalSince1970: 0))
            Settings.sessionCookie = nil
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == ServerDetailViewController.segueIdentifier {
            (segue.destination.children[0] as? ServerDetailViewController)?.serverId = lastSelectedServerId
        }
    }

    private func refreshCellItems() {
        // Refresh usage
        usageCellItems = []
        if let usage = BitletSynchronizer.shared.cachedBitlet(forKey: Usage.bitlet().cacheKey) as? Usage {
            usageCellItems.append(OverviewCellItem(withUsageLabel: NSLocalizedString("OVERVIEW_USAGE_DATA_TRAFFIC", comment: ""), andValue: usage.dataTraffic?.label ?? ""))
            usageCellItems.append(OverviewCellItem(withUsageLabel: NSLocalizedString("OVERVIEW_USAGE_SERVER_LOAD", comment: ""), andValue: usage.serverLoad?.label ?? ""))
        } else if BitletSynchronizer.shared.cacheState(forKey: Usage.bitlet().cacheKey) == .loading {
            usageCellItems.append(OverviewCellItem(withLoadingText: NSLocalizedString("OVERVIEW_USAGE_LOADING", comment: "")))
        } else {
            usageCellItems.append(OverviewCellItem(withErrorText: NSLocalizedString("OVERVIEW_USAGE_ERROR", comment: "")))
        }

        // Refresh server list
        serverCellItems = []
        if let serverList = BitletSynchronizer.shared.cachedBitlet(forKey: ServerList.bitlet().cacheKey) as? ServerList {
            for server in serverList.servers {
                serverCellItems.append(OverviewCellItem(withServerName: server.name ?? "", andLocation: server.location ?? "", enabled: server.enabled ?? false))
            }
        } else if BitletSynchronizer.shared.cacheState(forKey: ServerList.bitlet().cacheKey) == .loading {
            serverCellItems.append(OverviewCellItem(withLoadingText: NSLocalizedString("OVERVIEW_SERVER_LOADING", comment: "")))
        } else {
            serverCellItems.append(OverviewCellItem(withErrorText: NSLocalizedString("OVERVIEW_SERVER_ERROR", comment: "")))
        }
        
        // Refresh table view
        self.tableView.reloadData()
    }

    
    // --
    // MARK: IB Actions
    // --
    
    @objc @IBAction func pulledToRefresh() {
        loadData(forced: true)
        if BitletSynchronizer.shared.anyCacheInState(.loading, forKeys: [Usage.bitlet().cacheKey, ServerList.bitlet().cacheKey]) {
            refreshCellItems()
        }
    }
    
    
    // --
    // MARK: Data loading
    // --
    
    private func loadData(forced: Bool) {
        // Load usage
        let checkCaches = [Usage.bitlet().cacheKey, ServerList.bitlet().cacheKey]
        BitletSynchronizer.shared.loadBitlet(Usage.bitlet(), cacheKey: Usage.bitlet().cacheKey, forced: forced, completion: { usage, error in
            if let error = error {
                self.showErrorToast(error)
            }
            self.refreshCellItems()
            if forced && !BitletSynchronizer.shared.anyCacheInState(.loadingOrRefreshing, forKeys: checkCaches) {
                self.refreshControl?.endRefreshing()
            }
        })
        
        // Load server list
        BitletSynchronizer.shared.loadBitlet(ServerList.bitlet(), cacheKey: ServerList.bitlet().cacheKey, forced: forced, completion: { serverList, error in
            if let error = error {
                self.showErrorToast(error)
            }
            self.refreshCellItems()
            if forced && !BitletSynchronizer.shared.anyCacheInState(.loadingOrRefreshing, forKeys: checkCaches) {
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
    // MARK: UITableViewDelegate
    // --
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let serverList = BitletSynchronizer.shared.cachedBitlet(forKey: ServerList.bitlet().cacheKey) as? ServerList, indexPath.section == OverviewSection.server.rawValue {
            lastSelectedServerId = serverList.servers[indexPath.row].serverId
            performSegue(withIdentifier: ServerDetailViewController.segueIdentifier, sender: self)
        }
        tableView.deselectRow(at: indexPath, animated: false)
    }
    

    // --
    // MARK: UITableViewDataSource
    // --
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if let section = OverviewSection(rawValue: section) {
            if section == .usage {
                return NSLocalizedString("OVERVIEW_USAGE_SECTION", comment: "")
            } else if section == .server {
                return NSLocalizedString("OVERVIEW_SERVER_SECTION", comment: "")
            }
        }
        return nil
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let section = OverviewSection(rawValue: section) {
            if section == .usage {
                return usageCellItems.count
            } else if section == .server {
                return serverCellItems.count
            }
        }
        return 0
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let section = OverviewSection(rawValue: indexPath.section) {
            let cellItems = section == .server ? serverCellItems : usageCellItems
            let cellItem = cellItems[indexPath.row]
            if cellItem.type == .loading, let cell = tableView.dequeueReusableCell(withIdentifier: LoadingCell.cellIdentifier) as? LoadingCell {
                cell.selectionStyle = .none
                cell.label = cellItem.label
                return cell
            } else if cellItem.type == .error, let cell = tableView.dequeueReusableCell(withIdentifier: ErrorCell.cellIdentifier) as? ErrorCell {
                cell.selectionStyle = .none
                cell.label = cellItem.label
                return cell
            } else if cellItem.type == .usage, let cell = tableView.dequeueReusableCell(withIdentifier: OverviewCell.cellIdentifier) as? OverviewCell {
                cell.selectionStyle = .none
                cell.label = cellItem.label
                cell.value = cellItem.value
                return cell
            } else if cellItem.type == .server, let cell = tableView.dequeueReusableCell(withIdentifier: ServerCell.cellIdentifier) as? ServerCell {
                cell.label = cellItem.label
                cell.additional = cellItem.additional
                cell.value = cellItem.value
                return cell
            }
        }
        return UITableViewCell()
    }
    
}
