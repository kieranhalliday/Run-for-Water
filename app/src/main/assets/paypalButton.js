<div id="paypal-button"></div>

<script src="https://www.paypalobjects.com/api/checkout.js"></script>

<script>
    paypal.Button.render({

        env: 'production', // Or 'sandbox'

        client: {
            sandbox:    'AR780mPWSX9VpCW5RYRuqyMj6YgQuVf4RaQAkRzZPO0Aam9vf3gZJZ5ZcinI9hqNGwU3PU4jrKUYoSFQ',
            production: 'Ab-tteuCJM2-km7aaPhgRBldltkor8-0DziTc399VrAsuewuq0NQg3oV_eRCf0FncXUtisvfhDAPH9X2'
        },

        commit: true, // Show a 'Pay Now' button

        payment: function(data, actions) {
            return actions.payment.create({
                payment: {
                    transactions: [
                        {
                            amount: { total: '1.00', currency: 'USD' }
                        }
                    ]
                }
            });
        },

        onAuthorize: function(data, actions) {
            return actions.payment.execute().then(function(payment) {

                // The payment is complete!
                // You can now show a confirmation message to the customer
            });
        }

    }, '#paypal-button');
</script>