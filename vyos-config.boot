firewall {
    global-options {
        state-policy {
            established {
                action accept
            }
            invalid {
                action drop
            }
            related {
                action accept
            }
        }
    }
    group {
        network-group INTERNET-ALLOWED {
            network 10.10.10.0/24
            network 10.10.20.0/24
            network 10.20.20.0/24
            network 10.20.10.0/24
        }
        network-group IOT-NET {
            network 10.10.30.0/24
        }
        network-group IOT-ALLOWED-NET {
            network 10.10.30.11/32
        }
        network-group IOT-LIMITED-NET {
            network 10.10.30.12/32
        }
        network-group LAB-NET {
            network 10.10.20.0/24
        }
        network-group LAB-IOT-ALLOWED {
            network 10.10.20.11/32
        }
        network-group MGMT-NET {
            network 10.10.100.0/24
        }
        network-group USERS-NET {
            network 10.10.10.0/24
        }
        port-group HTTPS {
            port 443
        }
    }
    ipv4 {
        forward {
            filter {
                default-action drop
                rule 30 {
                    action accept
                    description "allow specific iot devices access to specific lab devices"
                    destination {
                        group {
                            network-group LAB-IOT-ALLOWED
                        }
                    }
                    source {
                        group {
                            network-group IOT-ALLOWED-NET
                        }
                    }
                }
                rule 31 {
                    action accept
                    description "Allow specific IoT devices https access to specific lab devices"
                    destination {
                        group {
                            network-group LAB-IOT-ALLOWED
                            port-group HTTPS
                        }
                    }
                    protocol tcp
                    source {
                        group {
                            network-group IOT-LIMITED-NET
                        }
                    }
                }
                rule 35 {
                    action accept
                    description "Allow specific lab devices access to IoT devices"
                    destination {
                        group {
                            network-group IOT-NET
                        }
                    }
                    source {
                        group {
                            network-group LAB-IOT-ALLOWED
                        }
                    }
                }
                rule 100 {
                    action accept
                    description "Allow MGMT access to network"
                    source {
                        group {
                            network-group MGMT-NET
                        }
                    }
                }
                rule 101 {
                    action accept
                    destination {
                        group {
                            network-group MGMT-NET
                        }
                    }
                    source {
                        address 10.20.10.10
                    }
                }
                rule 110 {
                    action accept
                    description "Allow approved devices internet access"
                    outbound-interface {
                        name eth2
                    }
                    source {
                        group {
                            network-group INTERNET-ALLOWED
                        }
                    }
                }
            }
        }
    }
}
interfaces {
    ethernet eth0 {
        description "EARTH-LAN VLAN TRUNK"
        hw-id 0c:f4:c4:3f:00:00
        offload {
            gro
        }
        vif 10 {
            address 10.10.10.1/24
            description USERS
        }
        vif 20 {
            address 10.10.20.1/24
            description LABS
        }
        vif 30 {
            address 10.10.30.1/24
            description IOT
        }
        vif 40 {
            address 10.10.40.1/24
        }
        vif 50 {
            address 10.10.50.1/24
        }
        vif 60 {
            address 10.10.60.1/24
        }
        vif 70 {
            address 10.10.70.1/24
        }
        vif 80 {
            address 10.10.80.1/24
        }
        vif 90 {
            address 10.10.90.1/24
        }
        vif 100 {
            address 10.10.100.1/24
            description MGMT
        }
    }
    ethernet eth1 {
        address 10.30.0.1/30
        description "TRANSIT UP"
        hw-id 0c:f4:c4:3f:00:01
        offload {
            gro
        }
    }
    ethernet eth2 {
        address dhcp
        description INTERNET-WAN
        hw-id 0c:f4:c4:3f:00:02
        offload {
            gro
        }
    }
    ethernet eth3 {
        hw-id 0c:f4:c4:3f:00:03
        offload {
            gro
        }
    }
    loopback lo {
        address 10.255.0.1/32
    }
    vti vti1 {
        address 10.255.255.1/30
    }
}
nat {
    source {
        rule 100 {
            outbound-interface {
                name eth2
            }
            source {
                address 10.10.0.0/16
            }
            translation {
                address masquerade
            }
        }
        rule 101 {
            outbound-interface {
                name eth2
            }
            source {
                address 10.20.0.0/24
            }
            translation {
                address masquerade
            }
        }
    }
}
protocols {
    ospf {
        area 0 {
            network 10.10.10.0/24
            network 10.254.0.0/30
            network 10.255.0.1/32
        }
        parameters {
            router-id 10.255.0.1
        }
    }
    static {
        route 10.20.0.0/16 {
            next-hop 10.255.255.2 {
            }
        }
    }
}
service {
    dns {
        forwarding {
            allow-from 10.10.10.0/24
            allow-from 10.10.20.0/24
            allow-from 10.10.30.0/24
            allow-from 10.20.30.0/24
            allow-from 10.20.20.0/24
            allow-from 10.20.10.0/24
            listen-address 10.10.10.1
            listen-address 10.10.20.1
            listen-address 10.10.30.1
            listen-address 10.10.100.1
            listen-address 10.20.30.1
            listen-address 10.20.20.1
            listen-address 10.20.10.1
            listen-address 10.20.100.1
            system
        }
    }
    ntp {
        allow-client {
            address 127.0.0.0/8
            address 169.254.0.0/16
            address 10.0.0.0/8
            address 172.16.0.0/12
            address 192.168.0.0/16
            address ::1/128
            address fe80::/10
            address fc00::/7
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
        commit-revisions 100
    }
    console {
        device ttyS0 {
            kernel
            speed 115200
        }
    }
    host-name EARTH-RTR-01
    login {
        operator-group default {
            command-policy {
                allow "*"
            }
        }
        user vyos {
            authentication {
                encrypted-password ****************
                plaintext-password ****************
            }
        }
    }
    name-server 192.168.42.1
    option {
        reboot-on-upgrade-failure 5
    }
    syslog {
        local {
            facility all {
                level info
            }
            facility local7 {
                level debug
            }
        }
    }
}
vpn {
    ipsec {
        authentication {
            psk EARTH-MOON {
                id 10.30.0.1
                id 10.30.0.2
                secret 8dd0856e147bbff145d7683fc588acb60238b6cea33637b26a8a02c94d42483b
                secret-type hex
            }
        }
        esp-group ESP {
            lifetime 3600
            mode tunnel
            pfs dh-group19
            proposal 10 {
                encryption aes256gcm128
                hash sha256
            }
        }
        ike-group IKE {
            close-action start
            dead-peer-detection {
                action restart
            }
            key-exchange ikev2
            lifetime 10800
            proposal 10 {
                dh-group 19
                encryption aes256
                hash sha256
                prf prfsha256
            }
        }
        interface eth1
        options {
            disable-route-autoinstall
        }
        site-to-site {
            peer MOON {
                authentication {
                    local-id 10.30.0.1
                    mode pre-shared-secret
                    remote-id 10.30.0.2
                }
                connection-type initiate
                default-esp-group ESP
                ike-group IKE
                local-address 10.30.0.1
                remote-address 10.30.0.2
                vti {
                    bind vti1
                    esp-group ESP
                }
            }
        }
    }
}
