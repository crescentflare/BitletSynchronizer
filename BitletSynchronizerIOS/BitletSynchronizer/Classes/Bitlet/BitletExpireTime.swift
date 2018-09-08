//
//  BitletExpireTime.swift
//  Bitlet Synchronizer Pod
//
//  Library: indicates bitlet expiry
//  A bitlet expire time can be speficied when it's used for caching
//

public struct BitletExpireTime: Comparable {
    
    // --
    // MARK: Constant
    // --

    public static let distantFuture: BitletExpireTime = BitletExpireTime(rawValue: Date.distantFuture.timeIntervalSince1970)
    

    // --
    // MARK: Member
    // --

    public let rawValue: TimeInterval
    

    // --
    // MARK: Initialization
    // --

    public init(rawValue: TimeInterval) {
        self.rawValue = rawValue
    }
    

    // --
    // MARK: Utility functions
    // --

    public static func now() -> BitletExpireTime {
        return BitletExpireTime(rawValue: Date().timeIntervalSince1970)
    }

    public static func secondsFromNow(_ seconds: Int) -> BitletExpireTime {
        return BitletExpireTime(rawValue: now().rawValue + Double(seconds) * 1000)
    }

    public static func minutesFromNow(_ minutes: Int) -> BitletExpireTime {
        return BitletExpireTime(rawValue: now().rawValue + Double(minutes) * 1000 * 60)
    }

    public static func hoursFromNow(_ hours: Int) -> BitletExpireTime {
        return BitletExpireTime(rawValue: now().rawValue + Double(hours) * 1000 * 60 * 60)
    }

    
    // --
    // MARK: Comparable implementation
    // --

    public static func < (lhs: BitletExpireTime, rhs: BitletExpireTime) -> Bool {
        return lhs.rawValue < rhs.rawValue
    }
    
}
