import { Component, OnInit } from '@angular/core';
import {SelectItem} from 'primeng/api';
import {ContractClient} from '../../service/contract-client.service';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Contract} from "../../model/contract.model";

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {

  levels : SelectItem[];
  selectedLevel: string;
  form: FormGroup;

  constructor(private fb: FormBuilder,
              private contracClient: ContractClient) {
    this.levels = [
      {label:'Basic', value:'Basic'},
      {label:'Classical', value:'Classical'},
      {label:'Comfort', value:'Comfort'}
    ];
  }

  ngOnInit() {
    this.form = this.fb.group({
        flightId: [''],
        arrivalDate: [''],
        ticketId: [''],
        email: [''],
      }
    );
  }

  submit() {
    this.contracClient.create$(new Contract(this.form.value.flightId, this.form.value.arrivalDate))
      .subscribe(contract => console.log(JSON.stringify(contract)));
  }
}
