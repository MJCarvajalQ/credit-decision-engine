const API_URL = 'http://localhost:8080/api/loan/decision';

export async function requestLoanDecision(personalCode, loanAmount, loanPeriod) {
    const response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ personalCode, loanAmount, loanPeriod }),
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Request failed');
    }

    return response.json();
}
