#!/bin/bash

USER_STORE="./src/data/user-store.txt"
TMP_STORE="./src/data/tmp_user_store.txt"
LIFE_EXPECTANCY_STORE="./src/data/life-expectancy.csv"

# Function to retrieve average lifespan by country
get_lifespan_by_country() {
    local country_code=$1
    awk -F, -v code="$country_code" '$4 == code {print $7}' $LIFE_EXPECTANCY_STORE
}
# Function to get patient data with labels
get_patient() {
    local email=$1
    local user_info
    user_info=$(grep "^$email," "$USER_STORE" | cut -d',' -f1-13)

    # Debugging output to check the extracted user_info
    echo "DEBUG: Extracted user_info: '$user_info'"

    if [[ -n "$user_info" ]]; then
        IFS=',' read -r email uuid password role first_name last_name dob has_hiv diagnosis_date on_art art_start_date country_iso survival_rate <<< "$user_info"

        echo "Email: $email"
        echo "UUID: $uuid"
        echo "Role: $role"
        echo "First Name: $first_name"
        echo "Last Name: $last_name"
        echo "Date of Birth: $dob"
        echo "Has HIV: $has_hiv"
        echo "Diagnosis Date: $diagnosis_date"
        echo "On ART: $on_art"
        echo "ART Start Date: $art_start_date"
        echo "Country ISO: $country_iso"
        echo "Survival Rate: $survival_rate"
    else
        echo "No data found for email: $email"
    fi
}


# Function to create user-store.txt if it doesn't exist
create_user_store() {
    if [ ! -f "$USER_STORE" ]; then
        touch "$USER_STORE"
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
    echo "$email,$uuid,,Patient" >> "$USER_STORE"
    echo "Initiated registration for $email with UUID: $uuid"
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
# Function to update patient data
update_patient_data() {
    local email=$2
    shift 2 # Remove the email from the argument list

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

            echo "$user_email,$user_uuid,$stored_password,$role,$new_first_name,$new_last_name,$new_dob,$new_has_hiv,$new_diagnosis_date,$new_on_art,$new_art_start_date,$new_country_code" >> "$TMP_STORE"
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

    while IFS=, read -r stored_email uuid stored_password role; do
        if [[ "$stored_email" == "$email" && "$stored_password" == "$hashed_password" ]]; then
            echo "Login successful,$email,$uuid,$role"
            return 0
        fi
    done < "$USER_STORE"

    echo "Login failed for $email"
    return 1
}

# Function to view all users
view_all_users() {
    # Header row
    printf "%-30s %-15s %-20s %-20s %-15s\n" "Email" "Role" "First Name" "Last Name" "Country ISO"

    # Read each line from the user store
    while IFS=',' read -r email uuid password role first_name last_name dob has_hiv diagnosis_date on_art art_start_date country_iso survival_rate; do
        # If the email is empty, skip the line
        if [[ -z "$email" ]]; then
            continue
        fi

        # Print each row with the defined column widths
        printf "%-30s %-15s %-20s %-20s %-15s\n" "$email" "$role" "$first_name" "$last_name" "$country_iso"
    done < "$USER_STORE"
}

# Function to compute survival rate and store it in the USER_STORE file
compute_survival_rate() {
    local email=$1
    local country_iso=$2
    local has_hiv=$3
    local diagnosis_date=$4
    local on_art=$5
    local art_start_date=$6

    # Default life expectancy (in case the country is not found in the CSV)
    local default_life_expectancy=75.0
    
    # File containing life expectancy data
    local file_path="./src/data/life-expectancy.csv"

    # Fetch life expectancy for the given country
    local life_expectancy=$(awk -F, -v iso="$country_iso" '$1 == iso {print $2}' "$file_path")
    
    # If the life expectancy is not found, use the default
    if [ -z "$life_expectancy" ]; then
        life_expectancy=$default_life_expectancy
    fi
    
    # Convert diagnosis date and ART start date to Unix timestamps
    local diagnosis_timestamp=$(date -d "$diagnosis_date" +"%s" 2>/dev/null || echo 0)
    local art_start_timestamp=$(date -d "$art_start_date" +"%s" 2>/dev/null || echo 0)
    local current_timestamp=$(date +"%s")
    
    # Calculate the age at diagnosis
    local dob_timestamp=$(date -d "$(grep -E "^$email," "$USER_STORE" | cut -d',' -f7)" +"%s" 2>/dev/null || echo 0)
    local age_at_diagnosis=$(( (diagnosis_timestamp - dob_timestamp) / 31536000 ))
    local years_since_diagnosis=$(( (current_timestamp - diagnosis_timestamp) / 31536000 ))
    
    # If the patient does not have HIV
    if [ "$has_hiv" == "false" ]; then
        echo "$life_expectancy"
        return
    fi
    
    # If the patient is not on ART, add 5 years to their age at diagnosis
    if [ "$on_art" == "false" ]; then
        echo "$((age_at_diagnosis + 5))"
        return
    fi
    
    # Calculate remaining lifespan after ART start
    local remaining_lifespan=$(echo "scale=2; ($life_expectancy - $age_at_diagnosis) * 0.9" | bc)
    for (( i=1; i<=years_since_diagnosis; i++ )); do
        if [ $i -gt 1 ]; then
            remaining_lifespan=$(echo "scale=2; $remaining_lifespan * 0.9" | bc)
        fi
    done
    
    # Final survival rate
    local survival_rate=$(echo "scale=2; $age_at_diagnosis + $remaining_lifespan" | bc)

    # Update the USER_STORE file with the survival rate
    awk -F, -v email="$email" -v sr="$survival_rate" 'BEGIN { OFS="," } $1 == email { print $1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,sr } $1 != email { print $0 }' "$USER_STORE" > tmp_file && mv tmp_file "$USER_STORE"
    
    echo "$survival_rate"
}



# Function to aggregate data
aggregate_data() {
    echo "Data aggregation functionality to be implemented"
}

# Function to download all users info
download_all_users() {
    # Define the output CSV file
    output_file="./src/data/all_users_info.csv"

    # Add the header row to the CSV file
    echo "Email,Role,First Name,Last Name,Country ISO" > "$output_file"

    # Read each line from the user store and append the selected fields to the CSV file
    while IFS=',' read -r email uuid password role first_name last_name dob has_hiv diagnosis_date on_art art_start_date country_iso survival_rate; do
        # If the email is empty, skip the line
        if [[ -z "$email" ]]; then
            continue
        fi

        # Append the selected fields to the CSV file
        echo "$email,$role,$first_name,$last_name,$country_iso" >> "$output_file"
    done < "$USER_STORE"

    echo "All users' information has been downloaded to $output_file"
}

# Function to calculate mean
calculate_mean() {
    local sum=0
    local count=0
    while IFS=, read -r email uuid password role first_name last_name dob has_hiv diagnosis_date on_art art_start_date country_iso survival_rate; do
        if [[ -n "$survival_rate" && "$survival_rate" != "Survival Rate" ]]; then
            sum=$(echo "$sum + $survival_rate" | bc)
            count=$((count + 1))
        fi
    done < "$USER_STORE"
    mean=$(echo "scale=2; $sum / $count" | bc)
    echo "$mean"
}

# Function to calculate median
calculate_median() {
    local survival_rates=($(awk -F, '{if(NR>1 && $13 != "") print $13}' "$USER_STORE" | sort -n))
    local count=${#survival_rates[@]}
    local mid=$((count / 2))
    if (( count % 2 == 0 )); then
        median=$(echo "scale=2; (${survival_rates[$mid-1]} + ${survival_rates[$mid]}) / 2" | bc)
    else
        median=${survival_rates[$mid]}
    fi
    echo "$median"
}

# Function to calculate mode
calculate_mode() {
    mode=$(awk -F, '{if(NR>1 && $13 != "") count[$13]++} END {for (val in count) {if (count[val] > max) {max = count[val]; mode = val}} print mode}' "$USER_STORE")
    echo "$mode"
}

# Function to calculate standard deviation
calculate_std_dev() {
    local mean=$(calculate_mean)
    local sum=0
    local count=0
    while IFS=, read -r email uuid password role first_name last_name dob has_hiv diagnosis_date on_art art_start_date country_iso survival_rate; do
        if [[ -n "$survival_rate" && "$survival_rate" != "Survival Rate" ]]; then
            diff=$(echo "$survival_rate - $mean" | bc)
            sq_diff=$(echo "$diff * $diff" | bc)
            sum=$(echo "$sum + $sq_diff" | bc)
            count=$((count + 1))
        fi
    done < "$USER_STORE"
    variance=$(echo "scale=2; $sum / $count" | bc)
    std_dev=$(echo "scale=2; sqrt($variance)" | bc)
    echo "$std_dev"
}

# Function to export analytics
export_analytics() {
    local mean=$(calculate_mean)
    local median=$(calculate_median)
    local mode=$(calculate_mode)
    local std_dev=$(calculate_std_dev)

    local output_file="./src/data/analytics_report.txt"

    {
        echo "Survival Rate Analytics Report"
        echo "=============================="
        echo "Mean: $mean"
        echo "Median: $median"
        echo "Mode: $mode"
        echo "Standard Deviation: $std_dev"
    } > "$output_file"

    echo "Analytics report has been generated at $output_file"
}

# Function to get and display life prognosis (survival rate) for a patient
get_life_prognosis() {
    local email=$1

    # Fetch patient data from USER_STORE
    local patient_info=$(get_patient "$email")

    if [[ -z "$patient_info" ]]; then
        echo "No data found for email: $email"
        return
    fi

    # Extract relevant details from patient_info
    local survival_rate=$(echo "$patient_info" | grep "Survival Rate:" | cut -d':' -f2 | xargs)

    if [[ -z "$survival_rate" ]]; then
        echo "Survival rate not available for email: $email"
    else
        echo "Survival Rate for $email: $survival_rate years"
    fi
}

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
    check_pre_registration)
        check_pre_registration "$2" "$3"
        ;;
    display_patient_info)
        display_patient_info "$2" "$3"
        ;;
    update_patient_data)
        update_patient_data "$@"
        ;;   
    get_lifespan)
        get_lifespan_by_country "$2"
        ;;
    check_login)
        check_login "$2" "$3"
        ;;
    get_patient)
        get_patient "$2"
        ;;
    compute_survival_rate)
        compute_survival_rate "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9" "${10}" "${11}"
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
    get_life_prognosis)
        get_life_prognosis "$2"
        ;;
    *)
        echo "Usage: $0 {create_store|initiate_registration|complete_registration|check_pre_registration|display_patient_info|update_patient_data|get_lifespan|check_login|get_patient|compute_survival_rate|view_all_users|aggregate_data|download_all_users|export_analytics|get_life_prognosis}"
        exit 1
        ;;
esac
