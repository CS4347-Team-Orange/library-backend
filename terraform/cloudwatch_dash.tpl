{
    "widgets": [
        {
            "height": 6,
            "width": 23,
            "y": 0,
            "x": 0,
            "type": "metric",
            "properties": {
                "metrics": [
                    [ "ECS/ContainerInsights", "RunningTaskCount", "ServiceName", "${service_name}", "ClusterName", "${cluster_name}" ],
                    [ ".", "DesiredTaskCount", ".", ".", ".", "." ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${region}",
                "annotations": {
                    "horizontal": [
                        {
                            "label": "MIN",
                            "value": 0.99
                        }
                    ]
                },
                "period": 300,
                "title": "Running Tasks"
            }
        },
        {
            "height": 6,
            "width": 23,
            "y": 6,
            "x": 0,
            "type": "metric",
            "properties": {
                "metrics": [
                    [ "ECS/ContainerInsights", "CpuUtilized", "ServiceName", "${service_name}", "ClusterName", "${cluster_name}", { "id": "m1" } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${region}",
                "title": "CPU Utilization",
                "annotations": {
                    "horizontal": [
                        {
                            "color": "#d62728",
                            "label": "HIGH",
                            "value": ${cpu_high},
                            "fill": "above"
                        }
                    ]
                },
                "period": 300,
                "stat": "Average"
            }
        },
        {
            "height": 6,
            "width": 23,
            "y": 12,
            "x": 0,
            "type": "metric",
            "properties": {
                "metrics": [
                    [ "ECS/ContainerInsights", "MemoryUtilized", "ServiceName", "${service_name}", "ClusterName", "${cluster_name}" ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${region}",
                "title": "Memory Utilization",
                "annotations": {
                    "horizontal": [
                        {
                            "color": "#d62728",
                            "label": "HIGH",
                            "value": ${ram_high},
                            "fill": "above"
                        }
                    ]
                },
                "period": 300
            }
        },
        {
            "height": 7,
            "width": 23,
            "y": 18,
            "x": 0,
            "type": "metric",
            "properties": {
                "metrics": [
                    [ "AWS/ApplicationELB", "HTTPCode_Target_4XX_Count", "TargetGroup", "${target_group}", "LoadBalancer", "${load_balancer}", { "id": "m1", "visible": false } ],
                    [ ".", "HTTPCode_Target_2XX_Count", ".", ".", ".", ".", { "id": "m2", "visible": false } ],
                    [ ".", "RequestCount", ".", ".", ".", ".", { "id": "m3", "label": "Total Requests", "color": "#1f77b4" } ],
                    [ { "expression": "IF(SUM(-[m3, e1]) > 0, SUM(-[m3, e1]), 0)", "label": "Failed Requests", "id": "e2", "region": "${region}", "color": "#d62728" } ],
                    [ { "expression": "SUM([m1, m2])", "label": "Successful Requests", "id": "e1", "color": "#2ca02c", "region": "${region}" } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${region}",
                "stat": "Sum",
                "period": 300,
                "title": "HTTP Request Success Rate"
            }
        },
        {
            "height": 6,
            "width": 23,
            "y": 25,
            "x": 0,
            "type": "metric",
            "properties": {
                "view": "timeSeries",
                "stacked": false,
                "metrics": [
                    [ "AWS/ApplicationELB", "TargetResponseTime", "TargetGroup", "${target_group}", "LoadBalancer", "${load_balancer}" ]
                ],
                "region": "${region}",
                "title": "Target Response Time",
                "yAxis": {
                    "left": {
                        "showUnits": true
                    }
                },
                "annotations": {
                    "horizontal": [
                        {
                            "label": "HIGH",
                            "value": 1,
                            "fill": "above"
                        }
                    ]
                }
            }
        }
    ]
}