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
            "height": 6,
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
            "y": 24,
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
        },
        {
            "type": "metric",
            "x": 0,
            "y": 32,
            "width": 23,
            "height": 6,
            "properties": {
                "metrics": [
                    [ "CloudWatchSynthetics", "Failed", "CanaryName", "library-${stage}-hc", { "label": "Failed", "color": "#d62728" } ],
                    [ ".", "2xx", ".", "library-${stage}-hc", { "label": "Success", "color": "#2ca02c" } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${region}",
                "stat": "Average",
                "period": 300,
                "title": "/actuator/health Canary"
            }
        },
        {
            "type": "metric",
            "x": 0,
            "y": 36,
            "width": 23,
            "height": 6,
            "properties": {
                "metrics": [
                    [ "CloudWatchSynthetics", "2xx", "CanaryName", "library-${stage}-api", { "label": "Success", "color": "#2ca02c" } ],
                    [ ".", "Failed", ".", ".", { "label": "Failed", "color": "#d62728" } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${region}",
                "stat": "Average",
                "period": 300,
                "title": "/api/books Canary"
            }
        }
    ]
}