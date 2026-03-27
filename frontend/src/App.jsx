import { useState } from 'react';
import { requestLoanDecision } from './api/loanApi';

function App() {
    const [personalCode, setPersonalCode] = useState('');
    const [loanAmount, setLoanAmount] = useState(5000);
    const [loanPeriod, setLoanPeriod] = useState(36);
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setResult(null);

        try {
            const data = await requestLoanDecision(personalCode, loanAmount, loanPeriod);
            setResult(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const isApproved = result?.decision === 'POSITIVE';

    return (
        <div className="container">
            <div className="card">
                <h1>Credit Decision Engine</h1>
                <p className="subtitle">Find out the maximum loan you qualify for</p>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="personalCode">Personal Code</label>
                        <input
                            id="personalCode"
                            type="text"
                            value={personalCode}
                            onChange={(e) => setPersonalCode(e.target.value)}
                            placeholder="e.g. 49002010987"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="loanAmount">Loan Amount (€)</label>
                        <input
                            id="loanAmount"
                            type="number"
                            value={loanAmount}
                            onChange={(e) => setLoanAmount(Number(e.target.value))}
                            min={2000}
                            max={10000}
                            required
                        />
                        <span className="hint">€2,000 – €10,000</span>
                    </div>

                    <div className="form-group">
                        <label htmlFor="loanPeriod">
                            Loan Period: <strong>{loanPeriod} months</strong>
                        </label>
                        <input
                            id="loanPeriod"
                            type="range"
                            value={loanPeriod}
                            onChange={(e) => setLoanPeriod(Number(e.target.value))}
                            min={12}
                            max={60}
                        />
                        <div className="range-labels">
                            <span>12 months</span>
                            <span>60 months</span>
                        </div>
                    </div>

                    <button type="submit" disabled={loading}>
                        {loading ? 'Processing...' : 'Get Decision'}
                    </button>
                </form>

                {error && (
                    <div className="result-card error">
                        <p>{error}</p>
                    </div>
                )}

                {result && (
                    <div className={`result-card ${isApproved ? 'approved' : 'rejected'}`}>
                        <div className="decision-badge">
                            {isApproved ? '✓ Approved' : '✗ Rejected'}
                        </div>

                        {isApproved && (
                            <>
                                <div className="result-detail">
                                    <span>Approved Amount</span>
                                    <strong>€{result.approvedAmount.toLocaleString()}</strong>
                                </div>
                                <div className="result-detail">
                                    <span>Approved Period</span>
                                    <strong>{result.approvedPeriod} months</strong>
                                </div>
                            </>
                        )}

                        <p className="result-message">{result.message}</p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default App;
