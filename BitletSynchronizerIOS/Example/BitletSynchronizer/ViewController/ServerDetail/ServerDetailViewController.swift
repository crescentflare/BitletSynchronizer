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
    
    private var loading = true
    private var server: Server?
    var serverId: String?

    
    // --
    // MARK: Lifecycle
    // --

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if loading {
            BitletSynchronizer.shared.loadBitlet(Server.bitlet(serverId: serverId ?? ""), completion: { server, error in
                self.loading = false
                if let error = error {
                    ToastManager.shared.position = .top
                    ToastManager.shared.duration = 4
                    if let toast = try? self.view.toastViewForMessage(error.localizedDescription, title: NSLocalizedString("ERROR_GENERIC_TITLE", comment: ""), image: nil, style: ToastManager.shared.style) {
                        UIApplication.shared.keyWindow?.showToast(toast)
                    }
                } else if server != nil {
                    self.server = server
                }
                self.tableView.reloadData()
            })
        }
    }

    
    // --
    // MARK: IB Actions
    // --
    
    @objc @IBAction func doneButtonPressed() {
        dismiss(animated: true, completion: nil)
    }
    
    @objc @IBAction func pulledToRefresh() {
        BitletSynchronizer.shared.loadBitlet(Server.bitlet(serverId: serverId ?? ""), completion: { server, error in
            if let error = error {
                ToastManager.shared.position = .top
                ToastManager.shared.duration = 4
                if let toast = try? self.view.toastViewForMessage(error.localizedDescription, title: NSLocalizedString("ERROR_GENERIC_TITLE", comment: ""), image: nil, style: ToastManager.shared.style) {
                    UIApplication.shared.keyWindow?.showToast(toast)
                }
            } else if server != nil {
                self.server = server
            }
            self.tableView.reloadData()
            self.refreshControl?.endRefreshing()
        })
    }

    
    // --
    // MARK: UITableViewDataSource
    // --
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return loading || server == nil ? 1 : 7
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if loading, let cell = tableView.dequeueReusableCell(withIdentifier: LoadingCell.cellIdentifier) as? LoadingCell {
            cell.label = NSLocalizedString("SERVER_DETAILS_LOADING", comment: "")
            return cell
        } else if server == nil, let cell = tableView.dequeueReusableCell(withIdentifier: ErrorCell.cellIdentifier) as? ErrorCell {
            cell.selectionStyle = .none
            cell.label = NSLocalizedString("SERVER_DETAILS_ERROR", comment: "")
            return cell
        } else if let cell = tableView.dequeueReusableCell(withIdentifier: ServerDetailCell.cellIdentifier) as? ServerDetailCell {
            if let row = DetailItemRow(rawValue: indexPath.row) {
                switch row {
                case .name:
                    cell.label = NSLocalizedString("SERVER_DETAILS_NAME", comment: "")
                    cell.value = server?.name ?? ""
                case .description:
                    cell.label = NSLocalizedString("SERVER_DETAILS_DESCRIPTION", comment: "")
                    cell.value = server?.description ?? ""
                case .operatingSystem:
                    cell.label = NSLocalizedString("SERVER_DETAILS_OPERATING_SYSTEM", comment: "")
                    cell.value = (server?.os ?? "") + " " + (server?.osVersion ?? "")
                case .location:
                    cell.label = NSLocalizedString("SERVER_DETAILS_LOCATION", comment: "")
                    cell.value = server?.location ?? ""
                case .dataTraffic:
                    cell.label = NSLocalizedString("SERVER_DETAILS_DATA_TRAFFIC", comment: "")
                    cell.value = server?.dataTraffic?.label ?? ""
                case .serverLoad:
                    cell.label = NSLocalizedString("SERVER_DETAILS_SERVER_LOAD", comment: "")
                    cell.value = server?.serverLoad?.label ?? ""
                case .enabled:
                    cell.label = NSLocalizedString("SERVER_DETAILS_ENABLED", comment: "")
                    cell.value = NSLocalizedString((server?.enabled ?? false) ? "SERVER_DETAILS_ENABLED_ON" : "SERVER_DETAILS_ENABLED_OFF", comment: "")
                }
            }
            return cell
        }
        return UITableViewCell()
    }

}
