(config
    (password-field
    :name "apiKey"
    :label "Subscription Key"
    :placeholder "Enter Subscription key"
    :required true
    )
)

(default-source (http/get :base-url "https://api.service.cqc.org.uk/public/v1"
                    (header-params "Accept" "application/json")
                )
                (paging/page-number 
                                    :page-number-query-param-initial-value 1
                                    :page-number-query-param-name "Page"
                                    :limit 100
                                    :limit-query-param-name "limit"
                )
                (auth/apikey-custom-header :headerName "Ocp-Apim-Subscription-Key")
                (error-handler
                    (when :status 404 
                        :message "not found" 
                        :action fail)
                    (when :status 400 
                        :message "Bad request" 
                        :action skip)
                    (when :status 500 
                        :message "Internal Server Error" 
                        :action retry 3)
                )
)

(temp-entity LOCATIONS
    (api-docs-url "https://api-portal.service.cqc.org.uk/api-details#api=syndication&operation=get-locations")
    (source (http/get 
                :url "/locations")
            (setup-test
                (upon-receiving 
                    :code 200 :action (pass)))
            (extract-path 
                "locations"))
    (field
        location_id : id :<= "localId"
        location_name :<= "locationName"
        postal_code :<= "postalCode")
)
