#!/bin/bash


#-----------------------------------------------------------------------------------------------------------------------------------
USER_STORE="./src/data/user-store.txt"
TMP_STORE="./src/data/tmp_user_store.txt"
LIFE_EXPECTANCY_STORE="./src/data/life-expectancy.csv"
#-----------------------------------------------------------------------------------------------------------------------------------
# Function to register a new patient
register_patient() { #admin class
    local email=$1
    local uuid=$(uuidgen)
    local role="Patient"

    # Append the new patient record to the user-store.txt
    echo "$email,$uuid,,${role}" >> "$USER_STORE"
    echo "Registered new patient with Email: $email and UUID: $uuid"
}


# Function to initiate registration
initiate_registration() {
    local email=$1
    local uuid=$(uuidgen)
    echo "$email,$uuid,,Admin" >> "$USER_STORE"
    echo "Initiated registration for $email with UUID: $uuid"
}
#-----------------------------------------------------------------------------------------------------------------------------------

# Function to view all users
view_all_users() { #Admin class
    cat "$USER_STORE"
}
#-------------------------------------------------------------------------------------------------------------------------------------

#Aggregate the user data in week3 

#-------------------------------------------------------------------------------------------------------------------------------------

# Function to download all users info
download_all_users() {
    cp "$USER_STORE" "./all_users_info.csv"
    echo "All users' information has been downloaded to all_users_info.csv"
}

#-----------------------------------------------------------------------------------------------------

#Export data in week3

#-----------------------------------------------------------------------------------------------------


# Function to complete registration
complete_registration() { #Patient class
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

    # Ensure atomic update by using a temporary file
    while IFS=, read -r email user_uuid stored_password role; do
        if [[ "$user_uuid" == "$uuid" ]]; then
            echo "$email,$uuid,$hashed_password,Patient,$first_name,$last_name,$dob,$has_hiv,$diagnosis_date,$on_art,$art_start_date,$country_code" >> "$TMP_STORE"
        else
            echo "$email,$user_uuid,$stored_password,$role" >> "$TMP_STORE"
        fi
    done < "$USER_STORE"

    mv "$TMP_STORE" "$USER_STORE"

    echo "Registration completed for UUID: $uuid"
}

#------------------------------------------------------------------------------------------------------------------------------------------------

# Function to get patient data
# get_patient() {
#     local email=$1
#     grep "^$email," "$USER_STORE" | cut -d',' -f5-
# }


# Function to get patient data
get_patient() {
    local email=$1

    # Check if the file exists
    if [[ ! -f "$USER_STORE" ]]; then
        echo "Error: User store file not found."
        return
    fi

    # Get the patient data
    local patient_data=$(grep "^$email," "$USER_STORE" | cut -d',' -f5-)

    if [[ -z "$patient_data" ]]; then
        echo "Error: Patient data not found."
    else
        echo "$patient_data"
    fi
}

#-------------------------------------------------------------------------------------------------------------------------------------------


# Function to retrieve average lifespan by country
get_lifespan_by_country() {
    local country_code=$1
    awk -F, -v code="$country_code" '$4 == code {print $7}' $LIFE_EXPECTANCY_STORE
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
#---------------------------------------------------------------------------------------------------------------------------------------------------
# Main script execution
case "$1" in
    create_store)
        create_user_store
        ;;
    initiate_registration)
        initiate_registration "$2"
        ;;
    complete_registration)
        complete_registration "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9" "${10}"
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
    *)
        echo "Usage: $0 {create_store|initiate_registration|complete_registration|get_lifespan|check_login|check_pre_registration|register_patient}"
        exit 1
        ;;
esac

exit 0
