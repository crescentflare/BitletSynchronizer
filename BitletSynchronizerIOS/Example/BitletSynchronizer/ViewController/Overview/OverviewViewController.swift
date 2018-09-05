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

private enum OverviewUsageRow: Int {
    
    case traffic = 0
    case load = 1
    
}

class OverviewViewController: UITableViewController {

    // --
    // MARK: Constants
    // --
    
    static let segueIdentifier = "showOverview"


    // --
    // MARK: Members
    // --
    
    private var loadingUsage = true
    private var loadingServers = true
    private var usage: Usage?
    private var serverList: ServerList?
    private var lastSelectedServerId: String?

    
    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if loadingUsage {
            BitletSynchronizer.shared.loadBitlet(Usage.bitlet(), completion: { usage, error in
                self.loadingUsage = false
                if let error = error {
                    ToastManager.shared.position = .top
                    ToastManager.shared.duration = 4
                    if let toast = try? self.view.toastViewForMessage(error.localizedDescription, title: NSLocalizedString("ERROR_GENERIC_TITLE", comment: ""), image: nil, style: ToastManager.shared.style) {
                        UIApplication.shared.keyWindow?.showToast(toast)
                    }
                } else if usage != nil {
                    self.usage = usage
                }
                self.tableView.reloadData()
            })
        }
        if loadingServers {
            BitletSynchronizer.shared.loadBitlet(ServerList.bitlet(), completion: { serverList, error in
                self.loadingServers = false
                if let error = error {
                    ToastManager.shared.position = .top
                    ToastManager.shared.duration = 4
                    if let toast = try? self.view.toastViewForMessage(error.localizedDescription, title: NSLocalizedString("ERROR_GENERIC_TITLE", comment: ""), image: nil, style: ToastManager.shared.style) {
                        UIApplication.shared.keyWindow?.showToast(toast)
                    }
                } else if serverList != nil {
                    self.serverList = serverList
                }
                self.tableView.reloadData()
            })
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if isMovingFromParentViewController {
            if let serverAddress = Settings.serverAddress {
                Alamofire.request(serverAddress + "/sessions/" + (Settings.sessionCookie ?? ""), method: .delete).response { response in
                    // No implementation
                }
            }
            Alamofire.HTTPCookieStorage.shared.removeCookies(since: Date(timeIntervalSince1970: 0))
            Settings.sessionCookie = nil
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == ServerDetailViewController.segueIdentifier {
            (segue.destination.childViewControllers[0] as? ServerDetailViewController)?.serverId = lastSelectedServerId
        }
    }


    // --
    // MARK: IB Actions
    // --
    
    @objc @IBAction func pulledToRefresh() {
        var callsBusy = 2
        BitletSynchronizer.shared.loadBitlet(Usage.bitlet(), completion: { usage, error in
            if let error = error {
                ToastManager.shared.position = .top
                ToastManager.shared.duration = 4
                if let toast = try? self.view.toastViewForMessage(error.localizedDescription, title: NSLocalizedString("ERROR_GENERIC_TITLE", comment: ""), image: nil, style: ToastManager.shared.style) {
                    UIApplication.shared.keyWindow?.showToast(toast)
                }
            } else if usage != nil {
                self.usage = usage
            }
            self.tableView.reloadData()
            callsBusy -= 1
            if callsBusy <= 0 {
                self.refreshControl?.endRefreshing()
            }
        })
        BitletSynchronizer.shared.loadBitlet(ServerList.bitlet(), completion: { serverList, error in
            if let error = error {
                ToastManager.shared.position = .top
                ToastManager.shared.duration = 4
                if let toast = try? self.view.toastViewForMessage(error.localizedDescription, title: NSLocalizedString("ERROR_GENERIC_TITLE", comment: ""), image: nil, style: ToastManager.shared.style) {
                    UIApplication.shared.keyWindow?.showToast(toast)
                }
            } else if serverList != nil {
                self.serverList = serverList
            }
            self.tableView.reloadData()
            callsBusy -= 1
            if callsBusy <= 0 {
                self.refreshControl?.endRefreshing()
            }
        })
    }
    
    
    // --
    // MARK: UITableViewDelegate
    // --
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == OverviewSection.server.rawValue && serverList != nil {
            lastSelectedServerId = serverList?.servers[indexPath.row].serverId
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
                if loadingUsage || usage == nil {
                    return 1
                }
                return 2
            } else if section == .server {
                if loadingServers || serverList == nil {
                    return 1
                }
                return serverList?.servers.count ?? 0
            }
        }
        return 0
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let section = OverviewSection(rawValue: indexPath.section) {
            if section == .usage {
                if loadingUsage, let cell = tableView.dequeueReusableCell(withIdentifier: LoadingCell.cellIdentifier) as? LoadingCell {
                    cell.selectionStyle = .none
                    cell.label = NSLocalizedString("OVERVIEW_USAGE_LOADING", comment: "")
                    return cell
                } else if usage == nil, let cell = tableView.dequeueReusableCell(withIdentifier: ErrorCell.cellIdentifier) as? ErrorCell {
                    cell.selectionStyle = .none
                    cell.label = NSLocalizedString("OVERVIEW_USAGE_ERROR", comment: "")
                    return cell
                } else if let cell = tableView.dequeueReusableCell(withIdentifier: OverviewCell.cellIdentifier) as? OverviewCell {
                    cell.selectionStyle = .none
                    if let row = OverviewUsageRow(rawValue: indexPath.row) {
                        if row == .traffic {
                            cell.label = NSLocalizedString("OVERVIEW_USAGE_DATA_TRAFFIC", comment: "")
                            cell.value = usage?.dataTraffic?.label ?? ""
                        } else if row == .load {
                            cell.label = NSLocalizedString("OVERVIEW_USAGE_SERVER_LOAD", comment: "")
                            cell.value = usage?.serverLoad?.label ?? ""
                        }
                    }
                    return cell
                }
            } else if section == .server {
                if loadingServers, let cell = tableView.dequeueReusableCell(withIdentifier: LoadingCell.cellIdentifier) as? LoadingCell {
                    cell.selectionStyle = .none
                    cell.label = NSLocalizedString("OVERVIEW_SERVER_LOADING", comment: "")
                    return cell
                } else if serverList == nil, let cell = tableView.dequeueReusableCell(withIdentifier: ErrorCell.cellIdentifier) as? ErrorCell {
                    cell.selectionStyle = .none
                    cell.label = NSLocalizedString("OVERVIEW_SERVER_ERROR", comment: "")
                    return cell
                } else if let cell = tableView.dequeueReusableCell(withIdentifier: ServerCell.cellIdentifier) as? ServerCell {
                    if indexPath.row < serverList?.servers.count ?? 0, let server = serverList?.servers[indexPath.row] {
                        cell.label = server.name ?? ""
                        cell.additional = server.location ?? ""
                        cell.value = NSLocalizedString((server.enabled ?? false) ? "OVERVIEW_SERVER_ENABLED" : "OVERVIEW_SERVER_DISABLED", comment: "")
                    }
                    return cell
                }
            }
        }
        return UITableViewCell()
    }
    
}
