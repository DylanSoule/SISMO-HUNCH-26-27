
interfaces {
    ethernet eth0 {
        description "MOON-LAN"
        hw-id "0c:c6:83:39:00:00"
        offload {
            gro
        }
        vif 10 {
            address "10.20.10.1/24"
            description "USER-PCS"
        }
    }
    ethernet eth1 {
        address "10.30.0.2/30"
        description "EARTH-MOON-LINK-A"
        hw-id "0c:c6:83:39:00:01"
        offload {
            gro
        }
    }
    ethernet eth2 {
        hw-id "0c:c6:83:39:00:02"
        offload {
            gro
        }
    }
    ethernet eth3 {
        hw-id "0c:c6:83:39:00:03"
        offload {
            gro
        }
    }
    loopback lo {
        address "10.255.0.2/32"
    }
    vti vti1 {
        address "10.255.255.2/30"
    }
}
protocols {
    ospf {
        area 0 {
            network "10.20.10.0/24"
            network "10.254.0.0/24"
            network "10.255.0.2/32"
        }
        parameters {
            router-id "10.255.0.2"
        }
    }
    static {
        route 0.0.0.0/0 {
            next-hop 10.30.0.1 {
            }
        }
        route 10.10.0.0/16 {
            next-hop 10.255.255.1 {
            }
        }
    }
}
service {
    ntp {
        allow-client {
            address "127.0.0.0/8"
            address "169.254.0.0/16"
            address "10.0.0.0/8"
            address "172.16.0.0/12"
            address "192.168.0.0/16"
            address "::1/128"
            address "fe80::/10"
            address "fc00::/7"
        }
        server time1.vyos.net {
        }
        server time2.vyos.net {
        }
        server time3.vyos.net {
        }
    }
}
system {
    config-management {
        commit-revisions "100"
    }
    console {
        device ttyS0 {
            kernel
            speed "115200"
        }
    }
    host-name "MOON-RTR-01"
    login {
        operator-group default {
            command-policy {
                allow "*"
            }
        }
        user vyos {
            authentication {
                encrypted-password "$6$rounds=656000$Smbrpq8DfjgmX81l$8lP13tuJq.hJbmAoqhc8JDkSBX0eTGU8EtRVwmoH0MRqCR3v.T490sTxpdbvsi3NU2uV7Yxdd.TOl5WpolE8i."
                plaintext-password ""
            }
        }
    }
    option {
        reboot-on-upgrade-failure "5"
    }
    syslog {
        local {
            facility all {
                level "info"
            }
            facility local7 {
                level "debug"
            }
        }
    }
}
vpn {
    ipsec {
        authentication {
            psk EARTH-MOON {
                id "10.30.0.1"
                id "10.30.0.2"
                secret "8dd0856e147bbff145d7683fc588acb60238b6cea33637b26a8a02c94d42483b"
                secret-type "hex"
            }
        }
        esp-group ESP {
            lifetime "3600"
            mode "tunnel"
            pfs "dh-group19"
            proposal 10 {
                encryption "aes256gcm128"
                hash "sha256"
            }
        }
        ike-group IKE {
            close-action "none"
            dead-peer-detection {
                action "clear"
            }
            key-exchange "ikev2"
            lifetime "10800"
            proposal 10 {
                dh-group "19"
                encryption "aes256"
                hash "sha256"
                prf "prfsha256"
            }
        }
        interface "eth1"
        options {
            disable-route-autoinstall
        }
        site-to-site {
            peer EARTH {
                authentication {
                    local-id "10.30.0.2"
                    mode "pre-shared-secret"
                    remote-id "10.30.0.1"
                }
                connection-type "none"
                default-esp-group "ESP"
                ike-group "IKE"
                local-address "10.30.0.2"
                remote-address "10.30.0.1"
                vti {
                    bind "vti1"
                    esp-group "ESP"
                }
            }
        }
    }
}

// Warning: Do not remove the following line.
// vyos-config-version: "bgp@8:broadcast-relay@1:cluster@2:config-management@1:conntrack@6:conntrack-sync@2:container@3:dhcp-relay@2:dhcp-server@11:dhcpv6-server@6:dns-dynamic@5:dns-forwarding@4:firewall@20:flow-accounting@3:https@7:ids@2:interfaces@34:ipoe-server@4:ipsec@14:isis@3:l2tp@10:lldp@3:mdns@1:monitoring@2:nat@8:nat66@3:nhrp@1:ntp@3:openconnect@3:openvpn@6:ospf@2:pim@1:pki@1:policy@9:pppoe-server@13:pptp@6:qos@3:quagga@12:reverse-proxy@3:rip@1:rpki@2:snmp@3:ssh@3:sstp@7:system@33:vpp@6:vrf@4:vrrp@4:vyos-accel-ppp@2:wanloadbalance@4:webproxy@2"
// Release version: 2026.09.17-0028-rolling
