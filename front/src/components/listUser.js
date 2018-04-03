import React, { Component } from 'react';


class ListUser extends React.Component {
    render() {
        return (
            <ul>
                {this.props.users.map(item => (
                    <li key={item.id}>{item.name} / {item.email}</li>
                ))}
            </ul>
        );
    }
}
export default ListUser;