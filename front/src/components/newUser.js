import React, { Component } from 'react';
import ListUser from './listUser';
import axios from 'axios';

class NewUser extends Component {
    constructor(props) {
        super(props);
        this.state = { users:[],email:'',name:''};
        this.handleChangeEmail = this.handleChangeEmail.bind(this);
        this.handleChangeName = this.handleChangeName.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }
    componentDidMount() {
        axios.get(`http://localhost:8080/api/all`)
            .then(res => {
                const users = res.data;
                this.setState( { users:users,email:'',name:''});
            })
    }
    handleSubmit(e) {
        e.preventDefault();
        if (!this.state.email.length) {
            return;
        }
        const newUser = {
            name: this.state.name,
            email: this.state.email
        };
        axios.post(`http://localhost:8080/api/add`,  newUser )
            .then(res => {
                console.log(res.data);
                newUser.id = res.data.id;
                this.setState(prevState => ({
                    users: prevState.users.concat(newUser),
                    email: '',
                    name:''
                }))
            });


    }
    handleChangeName(e) {
        this.setState({ name: e.target.value });
    }
    handleChangeEmail(e) {
        this.setState({ email: e.target.value });
    }
    render() {
        return (
            <div>
                <ListUser users={this.state.users}/>
                <form onSubmit={this.handleSubmit}>
                    <input type={"text"} name={"name"} placeholder={"Name"} onChange={this.handleChangeName} value={this.state.name}/>
                    <input type={"text"} name={"email"} placeholder={"Email"} onChange={this.handleChangeEmail} value={this.state.email}/>
                    <input type={"submit"}/>
                </form>
            </div>
        );
    }
}
export default NewUser;