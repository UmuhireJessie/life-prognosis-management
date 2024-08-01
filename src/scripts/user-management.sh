#!/bin/bash

USER_STORE="user-store.txt"

# Function to create user-store.txt if it doesn't exist
create_user_store() {
    if [ ! -f "$USER_STORE" ]; then
        touch "$USER_STORE"
        # Add initial admin user
        echo "admin@example.com,$(uuidgen),$(echo -n "admin123" | openssl dgst -sha256 -binary | base64),Admin" >> "$USER_STORE"
        echo "user-store.txt created with initial admin user."
    else
        echo "user-store.txt already exists."
    fi
}

# Function to initiate registration
initiate_registration() {
    local email=$1
    local uuid=$(uuidgen)
    echo "$email,$uuid" >> "$USER_STORE"
    echo "$uuid"
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
    
    # Update the user entry in user-store.txt
    sed -i "s/^.*$uuid.*$/$uuid,$first_name,$last_name,$hashed_password,$dob,$has_hiv,$diagnosis_date,$on_art,$art_start_date,$country_code,Patient/" "$USER_STORE"
    
    echo "Registration completed for UUID: $uuid"
}

# Function to check login credentials
check_login() {
    local email=$1
    local password=$2
    local hashed_password=$(echo -n "$password" | openssl dgst -sha256 -binary | base64)
    
    grep -q "^$email,.*$hashed_password" "$USER_STORE"
    return $?
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
    check_login)
        check_login "$2" "$3"
        ;;
    *)
        echo "Usage: $0 {create_store|initiate_registration|complete_registration|check_login}"
        exit 1
        ;;
esac

exit 0