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
        return now() + seconds
    }

    public static func minutesFromNow(_ minutes: Int) -> BitletExpireTime {
        return now() + minutes * 60
    }

    public static func hoursFromNow(_ hours: Int) -> BitletExpireTime {
        return now() + hours * 3600
    }


    // --
    // MARK: Operator overloads
    // --

    public static func +(left: BitletExpireTime, right: Int) -> BitletExpireTime {
        return BitletExpireTime(rawValue: left.rawValue + Double(right))
    }

    public static func +(left: BitletExpireTime, right: Double) -> BitletExpireTime {
        return BitletExpireTime(rawValue: left.rawValue + right)
    }
    
    public static func -(left: BitletExpireTime, right: Int) -> BitletExpireTime {
        return BitletExpireTime(rawValue: left.rawValue - Double(right))
    }
    
    public static func -(left: BitletExpireTime, right: Double) -> BitletExpireTime {
        return BitletExpireTime(rawValue: left.rawValue - right)
    }

    
    // --
    // MARK: Comparable implementation
    // --

    public static func < (lhs: BitletExpireTime, rhs: BitletExpireTime) -> Bool {
        return lhs.rawValue < rhs.rawValue
    }
    
}
