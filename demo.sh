
#!/bin/bash

# HealthSafe — Systems Integration Demo
# Run from the project root.

set -u

show_menu() {
    echo
    echo "======================================"
    echo "       HEALTHSAFE DEMONSTRATION"
    echo "======================================"
    echo "1. Stage 1 - Cleaned Ward Data"
    echo "2. Stage 2 - REST API Integration"
    echo "3. Stage 3 - ActiveMQ Topic"
    echo "4. Stage 4 - ActiveMQ Queue"
    echo "5. Check All Services"
    echo "0. Exit"
    echo "======================================"
}

pause_demo() {
    echo
    read -r -p "Press Enter to return to the menu..."
}

pretty_json() {
    python3 -m json.tool
}

stage_one() {
    echo
    echo "STAGE 1: DATA INGESTION"
    echo "Showing cleaned hospital ward records..."
    curl -fsS http://localhost:7030/wards | pretty_json
    echo
}

stage_two() {
    echo
    echo "STAGE 2: REST API INTEGRATION"

    echo
    echo "Ward W-01:"
    curl -fsS http://localhost:7031/wards/W-01 | pretty_json

    echo
    echo "Emergency level:"
    curl -fsS http://localhost:7032/alert-level | pretty_json

    echo
    echo "Staffing requirements:"
    curl -fsS http://localhost:7033/staffing/W-01 | pretty_json
}

stage_three() {
    echo
    echo "STAGE 3: ACTIVEMQ TOPIC"

    echo
    echo "Triggering a staffing event..."
    curl -fsS http://localhost:7033/staffing/W-01 | pretty_json

    sleep 2

    echo
    echo "Latest staffing event received by Ward Service:"
    grep "Ward service received staffing event" \
        /tmp/healthsafe-ward.log | tail -1
}

stage_four() {
    echo
    echo "STAGE 4: ACTIVEMQ QUEUE"

    echo
    echo "Reporting a ventilator failure..."

    curl -i -sS -X POST \
        http://localhost:7031/equipment-failure \
        -H "Content-Type: application/json" \
        -d '{"wardId":"W-01","equipment":"Ventilator","description":"Ventilator stopped working"}'

    echo
    sleep 2

    echo
    echo "Latest equipment alert received:"
    grep "Equipment alert received" \
        /tmp/healthsafe-equipment.log | tail -1
}

check_services() {
    echo
    echo "CHECKING HEALTHSAFE SERVICES"

    for port in 7030 7031 7032 7033 7034; do
        response=$(curl -sS --max-time 3 \
            "http://localhost:$port/health" 2>/dev/null)

        if [ "$response" = "OK" ]; then
            echo "Port $port: RUNNING"
        else
            echo "Port $port: NOT RESPONDING"
        fi
    done
}

while true; do
    show_menu
    read -r -p "Choose an option (0-5): " choice

    case "$choice" in
        1) stage_one; pause_demo ;;
        2) stage_two; pause_demo ;;
        3) stage_three; pause_demo ;;
        4) stage_four; pause_demo ;;
        5) check_services; pause_demo ;;
        0)
            echo "Thank you for watching my HealthSafe demo!"
            exit 0
            ;;
        *)
            echo "Invalid option. Choose 0-5."
            ;;
    esac
done
