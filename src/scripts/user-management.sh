#!/bin/bash

USER_STORE="./src/data/user-store.txt"
TMP_STORE="./src/data/tmp_user_store.txt"
LIFE_EXPECTANCY_STORE="./src/data/life-expectancy.csv"
PATIENT_INFO_STORE="./src/data/patient_info.csv"

# Function to retrieve average lifespan by country
get_lifespan_by_country() {
    local country_code=$1
    awk -F, -v code="$country_code" '$4 == code {print $7}' $LIFE_EXPECTANCY_STORE
}
# Function to get patient data
get_patient() {
    local email=$1
    grep "^$email," "$USER_STORE" | cut -d',' -f5-
}
# Function to create user-store.txt if it doesn't exist
create_user_store() {
    if [ ! -f "$USER_STORE" ]; then
        touch "$USER_STORE"
        echo "admin@example.com,$(uuidgen),$(echo -n 'admin123' | openssl dgst -sha256 -binary | base64),Admin" >> "$USER_STORE"
        echo "user-store.txt created with initial admin user."
    else
        echo "user-store.txt already exists."
    fi
}

# Function to initiate registration
initiate_registration() {
    local email=$1
    local uuid=$(uuidgen)
    echo "$email,$uuid,,Patient" >> "$USER_STORE"
    echo "Initiated registration for $email with UUID: $uuid"
}

# Function to complete registration
complete_registration() {
    local uuid=$1
    local first_name=$2
    local last_name=$3
    local password=$4
    local dob=$5
    local has_hiv=$6
    local diagnosis_date=$7
    local on_art=$8
    local art_start_date=$9
    local country_code=${10}

    local hashed_password=$(echo -n "$password" | openssl dgst -sha256 -binary | base64)

    local uuid_check_passed=false

    # Ensure atomic update by using a temporary file
    while IFS=, read -r email user_uuid stored_password role; do
        if [[ "$user_uuid" == "$uuid" ]]; then
            # Update only the password in user-store.txt if the UUID check passes
            echo "$email,$uuid,$hashed_password,$role" >> "$TMP_STORE"
            uuid_check_passed=true
        else
            echo "$email,$user_uuid,$stored_password,$role" >> "$TMP_STORE"
        fi
    done < "$USER_STORE"

    mv "$TMP_STORE" "$USER_STORE"

    if [ "$uuid_check_passed" = true ]; then
        # Store the rest of the information in patient_info.csv
        echo "$uuid,$first_name,$last_name,$email,$dob,$has_hiv,$diagnosis_date,$on_art,$art_start_date,$country_code" >> "$PATIENT_INFO_STORE"
        echo "Registration completed for UUID: $uuid"
    else
        echo "UUID check failed. Registration not completed."
    fi
}
# complete_registration() {
#     local uuid=$1
#     local first_name=$2
#     local last_name=$3
#     local password=$4
#     local dob=$5
#     local has_hiv=$6
#     local diagnosis_date=$7
#     local on_art=$8
#     local art_start_date=$9
#     local country_code=${10}

#     local hashed_password=$(echo -n "$password" | openssl dgst -sha256 -binary | base64)

#     # Ensure atomic update by using a temporary file
#     while IFS=, read -r email user_uuid stored_password role; do
#         if [[ "$user_uuid" == "$uuid" ]]; then
#             echo "$email,$uuid,$hashed_password,Patient,$first_name,$last_name,$dob,$has_hiv,$diagnosis_date,$on_art,$art_start_date,$country_code" >> "$TMP_STORE"
#         else
#             echo "$email,$user_uuid,$stored_password,$role" >> "$TMP_STORE"
#         fi
#     done < "$USER_STORE"

#     mv "$TMP_STORE" "$USER_STORE"

#     echo "Registration completed for UUID: $uuid"
# }

update_patient_data() {
    local email=$1
    shift

    # Default values to empty
    local first_name="" last_name="" dob="" has_hiv="" diagnosis_date="" on_art="" art_start_date="" country_code=""

    # Parse key=value pairs
    for arg in "$@"; do
        case $arg in
            first_name=*) first_name="${arg#*=}" ;;
            last_name=*) last_name="${arg#*=}" ;;
            dob=*) dob="${arg#*=}" ;;
            has_hiv=*) has_hiv="${arg#*=}" ;;
            diagnosis_date=*) diagnosis_date="${arg#*=}" ;;
            on_art=*) on_art="${arg#*=}" ;;
            art_start_date=*) art_start_date="${arg#*=}" ;;
            country_code=*) country_code="${arg#*=}" ;;
            *) echo "Invalid argument: $arg" ;;
        esac
    done

    # Ensure atomic update by using a temporary file
    while IFS=, read -r user_email user_uuid stored_password role existing_first_name existing_last_name existing_dob existing_has_hiv existing_diagnosis_date existing_on_art existing_art_start_date existing_country_code; do
        if [[ "$user_email" == "$email" ]]; then
            # Use provided values if available, otherwise keep the existing ones
            new_first_name=${first_name:-$existing_first_name}
            new_last_name=${last_name:-$existing_last_name}
            new_dob=${dob:-$existing_dob}
            new_has_hiv=${has_hiv:-$existing_has_hiv}
            new_diagnosis_date=${diagnosis_date:-$existing_diagnosis_date}
            new_on_art=${on_art:-$existing_on_art}
            new_art_start_date=${art_start_date:-$existing_art_start_date}
            new_country_code=${country_code:-$existing_country_code}

            echo "$email,$user_uuid,$stored_password,$role,$new_first_name,$new_last_name,$new_dob,$new_has_hiv,$new_diagnosis_date,$new_on_art,$new_art_start_date,$new_country_code" >> "$TMP_STORE"
        else
            echo "$user_email,$user_uuid,$stored_password,$role,$existing_first_name,$existing_last_name,$existing_dob,$existing_has_hiv,$existing_diagnosis_date,$existing_on_art,$existing_art_start_date,$existing_country_code" >> "$TMP_STORE"
        fi
    done < "$USER_STORE"

    mv "$TMP_STORE" "$USER_STORE"

    echo "Patient data updated for email: $email"
}

# Function to display patient info in a table format
display_patient_info() {
    local email=$1
    local password=$2

    if check_login "$email" "$password"; then
        local patient_info=$(get_patient "$email")

        if [ -n "$patient_info" ]; then
            # Extract patient data fields
            local first_name=$(echo "$patient_info" | cut -d',' -f1)
            local last_name=$(echo "$patient_info" | cut -d',' -f2)
            local dob=$(echo "$patient_info" | cut -d',' -f3)
            local has_hiv=$(echo "$patient_info" | cut -d',' -f4)
            local diagnosis_date=$(echo "$patient_info" | cut -d',' -f5)
            local on_art=$(echo "$patient_info" | cut -d',' -f6)
            local art_start_date=$(echo "$patient_info" | cut -d',' -f7)
            local country_code=$(echo "$patient_info" | cut -d',' -f8)

            # Display in table format
            printf "%-20s %-20s %-15s %-15s %-20s %-10s %-15s %-15s\n" "First_Name" "Last_Name" "DOB" "HIV Status" "Diagnosis_Date" "On ART" "ART Start Date" "Country Code"
            printf "%-20s %-20s %-15s %-15s %-20s %-10s %-15s %-15s\n" "$first_name" "$last_name" "$dob" "$has_hiv" "$diagnosis_date" "$on_art" "$art_start_date" "$country_code"
        else
            echo "Patient record not found."
        fi
    else
        echo "Invalid email or password."
    fi
}

# Function to check login credentials
check_login() {
    local email=$1
    local password=$2
    local hashed_password=$(echo -n "$password" | openssl dgst -sha256 -binary | base64)

    while IFS=, read -r stored_email uuid stored_password role first_name last_name dob has_hiv diagnosis_date on_art art_start_date country_code; do
        if [[ "$stored_email" == "$email" ]]; then
            if [[ "$role" == "Admin" ]]; then
                if [[ -z "$password" ]]; then
                    echo "Password required for Admin login"
                    return 1
                elif [[ "$stored_password" == "$hashed_password" ]]; then
                    echo "Login successful,$email,$uuid,$role"
                    return 0
                else
                    echo "Login failed for $email"
                    return 1
                fi
            elif [[ "$role" == "Patient" ]]; then
                if [[ -z "$stored_password" ]]; then
                    echo "Registration incomplete,$email,$uuid"
                    return 1
                elif [[ "$stored_password" == "$hashed_password" ]]; then
                    echo "Login successful,$email,$uuid,$role"
                    return 0
                else
                    echo "Login failed for $email"
                    return 1
                fi
            fi
        fi
    done < "$USER_STORE"

    echo "Login failed for $email"
    return 1
}

# Function to view all users
view_all_users() {
    cat "$USER_STORE"
}

# Function to aggregate data
aggregate_data() {
    echo "Data aggregation functionality to be implemented"
}

# Function to download all users info
download_all_users() {
    cp "$USER_STORE" "./all_users_info.csv"
    echo "All users' information has been downloaded to all_users_info.csv"
}

# Function to export analytics
export_analytics() {
    echo "Analytics export functionality to be implemented"
}

# Function to check pre-registration
check_pre_registration() {
    local email=$1
    local uuid=$2

    while IFS=, read -r stored_email stored_uuid stored_password role; do
        if [[ "$stored_email" == "$email" && "$stored_uuid" == "$uuid" ]]; then
            echo "Pre-registration check successful,$email,$uuid"
            return 0
        fi
    done < "$USER_STORE"

    echo "Email is not found"
    return 1
}

# Main script execution
case "$1" in
    create_store)
        create_user_store
        ;;
    initiate_registration)
        initiate_registration "$2"
        ;;
    complete_registration)
        complete_registration "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9" "${10}" "${11}"
        ;;
    display_patient_info)
        display_patient_info "$2" "$3"
        ;;
    update_patient_data)
        update_patient_data "$2" "${@:3}"
        ;;   
    get_lifespan)
        get_lifespan_by_country "$2"
        ;;
    check_login)
        check_login "$2" "$3"
        ;;
    view_all_users)
        view_all_users
        ;;
    download_all_users)
        download_all_users
        ;;
    get_patient)
        get_patient "$2"
        ;;
    check_pre_registration)
        check_pre_registration "$2" "$3"
        ;;
    register_patient)
        register_patient "$2"
        ;;
    get_patient)
        get_patient "$2"
        ;;
    calculate_life_expectancy)
        calculate_life_expectancy "$1"
        ;;
    view_all_users)
        view_all_users
        ;;
    aggregate_data)
        aggregate_data
        ;;
    download_all_users)
        download_all_users
        ;;
    export_analytics)
        export_analytics
        ;;
    *)
        echo "Usage: $0 {create_store|initiate_registration|complete_registration|calculate_life_expectancy|get_lifespan|check_login|get_patient|view_all_users|aggregate_data|download_all_users|export_analytics}"
        exit 1
        ;;
esac

exit 0